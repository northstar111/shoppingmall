package com.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.mapper.TbSeckillOrderMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import com.pyg.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;
    @Value("#{tbSeckillGoodsMapper}")
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;
    @Value("#{redisTemplate}")
    private RedisTemplate redisTemplate;
    @Value("#{idWorker}")
    private IdWorker idWorker;

    @Override
    public void submitOrder(Long seckillId, String userId) {
        // 1.读取缓存中的秒杀商品
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(seckillId);
        if (seckillGoods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("你来晚啦！商品已被抢光啦！！！");
        }

        // 1.2扣减库存
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

        // 2.是否被抢光
        if (seckillGoods.getStockCount() == 0) {
            //3.1是
            // 同步到数据库
            tbSeckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            // 清除缓存中的数据
            redisTemplate.boundHashOps("seckillGoodsList").delete(seckillId);
        } else {
            // 3.2否则，更新缓存
            redisTemplate.boundHashOps("seckillGoodsList").put(seckillId, seckillGoods);
        }

        // 3.生成订单
        TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
        tbSeckillOrder.setId(idWorker.nextId());
        tbSeckillOrder.setSeckillId(seckillId);
        tbSeckillOrder.setCreateTime(new Date());
        tbSeckillOrder.setMoney(seckillGoods.getCostPrice());
        tbSeckillOrder.setSellerId(seckillGoods.getSellerId());
        tbSeckillOrder.setUserId(userId);
        tbSeckillOrder.setStatus("0");

        // 4.写到缓存
        redisTemplate.boundHashOps("seckillOrder").put(userId, tbSeckillOrder);
    }

    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    @Override
    public void saveOrderToDb(String userId, Long orderId, String transactionId) {
        // 1.读取缓存中的订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (seckillOrder.getId().longValue() != orderId.longValue()) {
            throw new RuntimeException("订单不一致");
        }
        // 2.更新状态
        seckillOrder.setPayTime(new Date());
        seckillOrder.setStatus("1");
        seckillOrder.setTransactionId(transactionId);
        // 3.保存到数据库
        seckillOrderMapper.insert(seckillOrder);
        // 4.删除缓存
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }

    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        // 1.读取缓存中的订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder != null && seckillOrder.getId().longValue() == orderId.longValue()) {
            // 2.恢复库存
            TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(seckillOrder.getSeckillId());
            if (tbSeckillGoods != null) {
                tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount() + 1);
                redisTemplate.boundHashOps("seckillGoodsList").put(tbSeckillGoods.getId(), tbSeckillGoods);
            }
            // 3.删除缓存的订单
            redisTemplate.boundHashOps("seckillOrder").delete(userId);
        }
    }

}
