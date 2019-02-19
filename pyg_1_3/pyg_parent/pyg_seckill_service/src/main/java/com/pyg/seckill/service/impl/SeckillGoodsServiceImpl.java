package com.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillGoodsExample;
import com.pyg.pojo.TbSeckillGoodsExample.Criteria;
import com.pyg.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Value("#{redisTemplate}")
    private RedisTemplate redisTemplate;

    @Override
    public List<TbSeckillGoods> findList() {
        // 1.获取缓存中的秒杀商品
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoodsList").values();
        // 2.如果缓存中不存在，就从数据库中获取
        if (seckillGoodsList == null || seckillGoodsList.size() == 0) {
            TbSeckillGoodsExample example = new TbSeckillGoodsExample();
            Criteria criteria = example.createCriteria();
            // 审核通过
            criteria.andStatusEqualTo("1");
            // 库存量大于0
            criteria.andStockCountGreaterThan(0);
            // 在当前时间有效的
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            criteria.andEndTimeGreaterThan(new Date());
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            // 3.再将商品存入缓存
            for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
                // 按id存放，便于详情页获取
                redisTemplate.boundHashOps("seckillGoodsList").put(tbSeckillGoods.getId(), tbSeckillGoods);
            }
        }
        return seckillGoodsList;
    }

    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
    }

}
