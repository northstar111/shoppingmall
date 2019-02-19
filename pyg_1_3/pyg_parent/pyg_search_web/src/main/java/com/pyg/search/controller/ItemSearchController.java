package com.pyg.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 作者：杨立波  时间：2018-09-14 10:28
 * 项目：pyg_parent
 * 说明：搜索控制器
 */
@RestController
@RequestMapping("itemSearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 功能：搜索方法
     * 参数：前台传递参数
     * 返回：返回搜索结果给页面
     */
    @RequestMapping("search")
    public Map<String, Object> search(@RequestBody  Map searchMap) {
        return itemSearchService.search(searchMap);
    }
}
