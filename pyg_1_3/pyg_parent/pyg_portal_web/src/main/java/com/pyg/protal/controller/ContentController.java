package com.pyg.protal.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbContent;
import com.pyg.content.service.ContentService;

import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/content")
public class ContentController {

	@Reference
	private ContentService contentService;

	/**
	 * 获取分类广告
	 */
	@RequestMapping("/findListByCategoryId")
	public List<TbContent> findListByCategoryId(Long categoryId){
		return contentService.findListByCategoryId(categoryId);
	}
}
