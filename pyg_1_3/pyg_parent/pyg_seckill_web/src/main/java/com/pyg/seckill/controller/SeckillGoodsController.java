package com.pyg.seckill.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.seckill.service.SeckillGoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

	@Reference
	private SeckillGoodsService seckillGoodsService;

	/**
	 * 获取当前正在参与秒杀的商品
	 *
	 * @return 秒杀商品列表
	 */
	@RequestMapping("/findList")
	public List<TbSeckillGoods> findList(){
		return seckillGoodsService.findList();
	}

	/**
	 * 从缓存中获取秒杀商品
	 *
	 * @param id 秒杀商品id
	 * @return 秒杀商品
	 */
	@RequestMapping("/findOneFromRedis")
	public TbSeckillGoods findOneFromRedis(Long id) {
		return seckillGoodsService.findOneFromRedis(id);
	}
}
