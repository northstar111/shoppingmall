package com.pyg.task;

import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 秒杀定时任务
 *
 * @author 杨立波  2018-09-28 9:13
 */

@Component
public class SeckillTask {

    @Value("#{tbSeckillGoodsMapper}")
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;
    @Value("#{redisTemplate}")
    private RedisTemplate redisTemplate;

    /**
     * 定时获取数据库中的增量数据到缓存
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void refreshSeckillGoods() {
        System.out.println(new Date());
        // 1.获取数据库中的增量商品
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStockCountGreaterThan(0);
        criteria.andStatusEqualTo("1");
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThan(new Date());
        // 不在缓存中的商品
        Set ids = redisTemplate.boundHashOps("seckillGoodsList").keys();
        if (ids != null && ids.size() > 0) {
            criteria.andIdNotIn(new ArrayList<>(ids));
        }
        List<TbSeckillGoods> seckillGoodsList = tbSeckillGoodsMapper.selectByExample(example);

        // 2.将商品放入缓存
        if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
            for (TbSeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(), seckillGoods);
                System.out.println("商品放入缓存，id：" + seckillGoods.getId());
            }
            System.out.println("放入缓存数量：" + seckillGoodsList.size());
        }
    }

    /**
     * 清除缓存中的已过期秒杀商品
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void removeSeckillGoods() {
        System.out.println(new Date());
        // 1.读取缓存中的商品
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoodsList").values();
        // 2.判断是否过期，并删除过期商品
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            if (seckillGoods.getEndTime().getTime() < System.currentTimeMillis()) {
                redisTemplate.boundHashOps("seckillGoodsList").delete(seckillGoods.getId());
                System.out.println("清除缓存中商品，id：" + seckillGoods.getId());
            }
        }
    }
}
