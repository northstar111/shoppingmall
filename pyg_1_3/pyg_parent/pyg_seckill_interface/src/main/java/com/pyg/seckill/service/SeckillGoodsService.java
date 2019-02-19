package com.pyg.seckill.service;
import java.util.List;
import com.pyg.pojo.TbSeckillGoods;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService {

	/**
	 * 从缓存中获取当前正在参与秒杀的商品，
	 * 如果缓存中不存在，就从数据库中获取，然后存入缓存
	 *
	 * @return 秒杀商品列表
	 */
	public List<TbSeckillGoods> findList();

	/**
	 * 从缓存中获取秒杀商品
	 *
	 * @param id 秒杀商品id
	 * @return 秒杀商品
	 */
	public TbSeckillGoods findOneFromRedis(Long id);
}
