package com.pyg.seckill.controller;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillOrderService;

import entity.PageResult;
import entity.Result;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 提交订单到缓存
     * @param seckillId 秒杀商品id
     * @return 操作结果
     */
    @RequestMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        // 获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(userId)) {
            return new Result(false, "用户未登录");
        }
        // 提交订单
        try {
            seckillOrderService.submitOrder(seckillId, userId);
            return new Result(true, "订单已提交");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "提交失败");
        }
    }


}
