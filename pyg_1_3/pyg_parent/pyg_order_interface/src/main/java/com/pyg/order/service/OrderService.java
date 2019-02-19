package com.pyg.order.service;
import java.util.List;
import com.pyg.pojo.TbOrder;

import com.pyg.pojo.TbPayLog;
import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 新增订单
	 *
	 * @param order 订单
	 */
	public void add(TbOrder order);

	/**
	 * 根据用户名，从redis中查询支付日志
	 *
	 * @param userId 用户名
	 * @return 支付日志
	 */
	public TbPayLog searchPayLog(String userId);

	/**
	 * 修改订单状态
	 *
	 * @param out_trade_no 支付订单号
	 * @param transaction_id 微信返回的交易流水号
	 */
	public void updateOrderStatus(String out_trade_no, String transaction_id);
	
}
