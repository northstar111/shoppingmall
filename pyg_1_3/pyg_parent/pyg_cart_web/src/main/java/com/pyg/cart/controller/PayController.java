package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.order.service.OrderService;
import com.pyg.pay.service.PayService;
import com.pyg.pojo.TbPayLog;
import com.pyg.util.IdWorker;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
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
    private OrderService orderService;

    /**
     * 发送支付请求生成生成微信支付url
     * 调用订单服务获取参数
     *
     * @return Map
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLog(userId);
        if (tbPayLog!=null) {
            return payService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee().toString());
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
        // 查询次数计数器
        int count = 0;
        while (true) {
            Map<String, String> map = payService.queryPayStatus(out_trade_no);
            if (map == null) {
                return new Result(false, "支付出错");
            }
            if (map.get("trade_state").equals("SUCCESS")) {
                // 更新订单
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
                return new Result(true, "支付成功");
            }
            count++;
            // 1分钟内未返回即认为失败
            if (count==20){
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
