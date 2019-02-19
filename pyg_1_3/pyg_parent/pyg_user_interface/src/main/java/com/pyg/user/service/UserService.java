package com.pyg.user.service;

import com.pyg.pojo.TbUser;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {
	/**
	 * 添加用户
	 *
	 * @param user 用户
	 */
	public void add(TbUser user);

	/**
	 * 发送短信验证码
	 *
	 * @param phone 手机号
	 * @return 操作结果
	 */
	public void sendCode(String phone);
}
