package com.pyg.cart.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbAddress;
import com.pyg.user.service.AddressService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;

	/**
	 * 增加
	 *
	 * @param address
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbAddress address){
		try {
			String userId = SecurityContextHolder.getContext().getAuthentication().getName();
			address.setUserId(userId);
			addressService.add(address);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 *
	 * @param address
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbAddress address){
		try {
			addressService.update(address);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	

	/**
	 * 删除
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long id){
		try {
			addressService.delete(id);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 获取收件人地址
	 *
	 * @return 收件人地址
	 */
	@RequestMapping("/findAddressListByUserId")
	public List<TbAddress> findAddressListByUserId() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return addressService.findAddressListByUserId(username);
	}
	
}
