package com.pyg.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbUser;
import com.pyg.user.service.UserService;
import com.pyg.util.PhoneFormatCheckUtils;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user, String checkCode){
		try {
			// 判断验证码是否一致
			String smsCode = (String) redisTemplate.boundHashOps("smsCode").get(user.getPhone());
			if (!checkCode.equals(smsCode)){
				return new Result(false, "注册码不正确");
			}
			userService.add(user);
			return new Result(true, "注册成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "注册失败");
		}
	}

	/**
	 * 发送验证码
	 * @param phone 手机号
	 * @return 操作结果
	 */
	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		// 校验手机号是否合法
		if (!PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
			return new Result(false, "手机号不合法");
		}
		try {
			userService.sendCode(phone);
			return new Result(true, "验证码已发送");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "验证码发送失败");
		}

	}
}
