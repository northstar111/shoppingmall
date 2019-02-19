package com.pyg.search.service;

import com.pyg.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * 作者：杨立波  时间：2018-09-14 10:15
 * 项目：pyg_parent
 * 说明：搜索服务接口
 */
public interface ItemSearchService {

    /**
     * 功能：搜索服务
     * 参数：前台传递的参数，Map类型
     * 返回：Map类型返回值，包含分页数据等信息
     */
    public Map<String, Object> search(Map searchMap);
}
