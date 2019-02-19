package com.pyg.user.service;
import java.util.List;
import com.pyg.pojo.TbAddress;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface AddressService {

	/**
	 * 增加
	*/
	public void add(TbAddress address);
	
	
	/**
	 * 修改
	 */
	public void update(TbAddress address);

	
	/**
	 * 删除
	 *
	 * @param id 地址id
	 */
	public void delete(Long id);

	/**
	 * 根据用户名获取收件地址
	 *
	 * @param userId 用户名
	 * @return 收件地址列表
	 */
	public List<TbAddress> findAddressListByUserId(String userId);
}
