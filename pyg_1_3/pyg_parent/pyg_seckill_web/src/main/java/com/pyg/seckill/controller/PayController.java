package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pay.service.PayService;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付控制层
 *
 * @author 杨立波  2018-09-22 17:48
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;
    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 发送支付请求生成微信二维码
     *
     * @return Map
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        // 获取秒杀订单信息
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        // 生成支付url
        if (tbSeckillOrder != null) {
            long fen = (long) (tbSeckillOrder.getMoney().doubleValue() * 100);
            return payService.createNative(tbSeckillOrder.getId() + "", fen + "");
        }
        return null;
    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no 订单号
     * @return Result
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // 支付时间计数器
        int count = 0;
        while (true) {
            Map<String, String> map = payService.queryPayStatus(out_trade_no);
            if (map == null) {
                return new Result(false, "支付出错");
            }
            if (map.get("trade_state").equals("SUCCESS")) {
                // 更新订单
                seckillOrderService.saveOrderToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                return new Result(true, "支付成功");
            }
            count++;
            // 支付时间：半分钟
            if (count == 10) {
                // 关闭微信支付
                Map<String, String> closeMap = payService.closePay(out_trade_no);
                // 检查返回信息
                if ("FAIL".equals(closeMap.get("return_code"))) {
                    // 如果订单已支付，则更新订单
                    if ("ORDERPAID".equals(closeMap.get("err_code"))) {
                        // 更新订单
                        seckillOrderService.saveOrderToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                        return new Result(true, "支付成功");
                    }
                }
                // 其他情况，删除订单
                seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                return new Result(false, "支付超时");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
