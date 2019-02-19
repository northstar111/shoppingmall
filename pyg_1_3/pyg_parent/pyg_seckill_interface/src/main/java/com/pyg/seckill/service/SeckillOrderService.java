package com.pyg.seckill.service;
import java.util.List;
import com.pyg.pojo.TbSeckillOrder;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 秒杀商品下单
	 *
	 * @param seckillId 秒杀商品ID
	 * @param userId 用户
	 */
	public void submitOrder(Long seckillId, String userId);

	/**
	 * 根据用户名查询缓存中的订单
	 *
	 * @param userId 用户名
	 * @return 订单
	 */
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId);

	/**
	 * 支付成功后，将缓存中的订单保存到数据库
	 *
	 * @param userId 用户名
	 * @param orderId 订单编号
	 * @param transactionId 交易流水号
	 */
	public void saveOrderToDb(String userId, Long orderId, String transactionId);

	/**
	 * 超时未支付，则将缓存中的订单记录删除
	 *
	 * @param userId 用户名
	 * @param orderId 订单编号
	 */
	public void deleteOrderFromRedis(String userId, Long orderId);
}
