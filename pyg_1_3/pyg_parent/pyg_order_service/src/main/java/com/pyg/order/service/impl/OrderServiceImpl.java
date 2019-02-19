package com.pyg.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pyg.mapper.TbOrderItemMapper;
import com.pyg.mapper.TbPayLogMapper;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojo.TbPayLog;
import com.pyg.pojogroup.Cart;
import com.pyg.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbOrderMapper;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbOrderExample;
import com.pyg.pojo.TbOrderExample.Criteria;
import com.pyg.order.service.OrderService;

import entity.PageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;
    @Value("#{tbOrderItemMapper}")
    private TbOrderItemMapper tbOrderItemMapper;
    @Value("#{redisTemplate}")
    private RedisTemplate redisTemplate;
    @Value("#{idWorker}")
    private IdWorker idWorker;
    @Value("#{tbPayLogMapper}")
    private TbPayLogMapper tbPayLogMapper;

    /**
     * 新增订单
     *
     * @param order 订单
     */
    @Override
    public void add(TbOrder order) {
        // 1.从redis中读取当前用户的购物车列表
        // 没有加判断，因为如果没有购物车的话就不会到达这里
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        // 4.1.1订单编号列表
        List<String> orderIdList = new ArrayList<>();
        // 4.1.2支付金额
        double totalFee = 0.0;

        // 2.遍历购物车列表，生成订单和订单明细
        for (Cart cart : cartList) {
            // 2.1生成订单
            TbOrder tbOrder = new TbOrder();
            // 采用雪花算法生成订单id
            Long orderId = idWorker.nextId();
            tbOrder.setOrderId(orderId);
            tbOrder.setUserId(order.getUserId());
            // 默认未付款
            tbOrder.setStatus("1");
            tbOrder.setSellerId(cart.getSellerId());
            tbOrder.setSourceType("2");
            tbOrder.setCreateTime(new Date());
            tbOrder.setUpdateTime(new Date());
            tbOrder.setPaymentType(order.getPaymentType());
            // 填写收件人信息
            tbOrder.setReceiver(order.getReceiver());
            tbOrder.setReceiverMobile(order.getReceiverMobile());
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());
            // 订单金额
            double payment = 0.0;

            for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
                // 2.2补全订单明细
                // 订单明细id
                Long orderItemId = idWorker.nextId();
                tbOrderItem.setId(orderItemId);
                // 订单主表id
                tbOrderItem.setOrderId(orderId);
                // 计算订单总金额
                payment += tbOrderItem.getTotalFee().doubleValue();
                // 2.3保存订单明细
                tbOrderItemMapper.insert(tbOrderItem);
            }
            // 填写订单金额
            tbOrder.setPayment(new BigDecimal(payment));
            // 2.4保存订单
            orderMapper.insert(tbOrder);

            // 4.1.1.2记录子订单
            orderIdList.add(orderId + "");
            // 4.1.2.2记录支付金额
            totalFee += payment;
        }
        // 4.如果是微信支付，则生成支付日志（父订单）
        if ("1".equals(order.getPaymentType())) {
            // 4.1添加订单内容
            TbPayLog tbPayLog = new TbPayLog();
            tbPayLog.setOutTradeNo(idWorker.nextId() + "");
            tbPayLog.setCreateTime(new Date());
            tbPayLog.setTotalFee((long) (totalFee * 100));
            tbPayLog.setUserId(order.getUserId());
            tbPayLog.setPayType("1");
            // 将订单号列表转为字符串
            String replace = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            tbPayLog.setOrderList(replace);
            tbPayLog.setTradeState("0");
            // 4.2保存父订单
            tbPayLogMapper.insert(tbPayLog);
            // 4.3放入缓存（生成二维码）
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), tbPayLog);
        }

        // 3.清除redis中当前用户的购物车列表
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    @Override
    public TbPayLog searchPayLog(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        // 1.更新父订单状态
        TbPayLog tbPayLog = tbPayLogMapper.selectByPrimaryKey(out_trade_no);
        tbPayLog.setPayTime(new Date());
        tbPayLog.setTradeState("1");
        tbPayLog.setTransactionId(transaction_id);
        tbPayLogMapper.updateByPrimaryKey(tbPayLog);

        // 2.更新子订单状态
        String[] orderIdArray = tbPayLog.getOrderList().split(",");
        for (String orderId : orderIdArray) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            if (tbOrder != null) {
                tbOrder.setUpdateTime(new Date());
                tbOrder.setPaymentTime(new Date());
                tbOrder.setStatus("2");
                orderMapper.updateByPrimaryKey(tbOrder);
            }
        }

        // 3.清除redis缓存
        redisTemplate.boundHashOps("payLog").delete(tbPayLog.getUserId());
    }

}
