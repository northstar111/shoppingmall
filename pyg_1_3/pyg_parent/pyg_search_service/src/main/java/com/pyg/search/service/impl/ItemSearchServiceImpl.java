package com.pyg.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.pojo.TbBrand;
import com.pyg.pojo.TbItem;
import com.pyg.pojogroup.Specification;
import com.pyg.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：杨立波  时间：2018-09-14 10:19
 * 项目：pyg_parent
 * 说明：搜索服务实现类
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Value("#{solrTemplate}")
    private SolrTemplate solrTemplate;
    @Value("#{redisTemplate}")
    private RedisTemplate redisTemplate;

    /**
     * 功能：通用搜索方法
     * 参数：前台传递参数
     * 返回：页面显示数据
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
        // 返回结果对象
        Map<String, Object> resultMap = new HashMap<>();

        //处理多余的空格
        String keywords = ((String) searchMap.get("keywords"));
        if (keywords != null) {
            keywords = keywords.replace(" ", "");
        }
        searchMap.put("keywords", keywords);

        // 1. 高亮显示结果
        resultMap.putAll(searchHighlightItemList(searchMap));

        // 2. 查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);

        // 3. 查询品牌和规格
        // 如果前台传递了分类，则根据该分类查询
        // 否则，默认按分类列表的第一个分类查询
        String category = (String) searchMap.get("category");
        if (StringUtils.isNotEmpty(category)) {
            resultMap.putAll(searchBrandAndSpecList(category));
        } else {
            if (categoryList.size() > 0) {
                resultMap.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }

        return resultMap;

    }

    /**
     * 功能：根据商品分类查询对应的品牌和规格信息
     * 参数：商品分类名称
     * 返回：品牌和规格列表
     */
    private Map<String, Object> searchBrandAndSpecList(String category) {
        // 返回结果
        Map<String, Object> map = new HashMap<>();

        // 1. 获取模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCatList").get(category);

        // 2. 根据模板id，获取品牌和规格
        if (typeId != null) {
            // 取品牌列表
            List<TbBrand> brandList = (List<TbBrand>) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);
            // 获取规格列表
            List<Specification> specList = (List<Specification>) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }

        return map;
    }


    /**
     * 功能：根据关键字查询商品分类
     * 参数：搜索条件
     * 返回：商品分类列表
     */
    private List<String> searchCategoryList(Map searchMap) {
        // 返回结果
        List<String> categoryList = new ArrayList<>();
        // 1. 创建查询对象
        Query query = new SimpleQuery();

        // 设置分词后的结果为与的关系
        // edismax查询解析器支持将多个单词增强为连续的单词对
        query.setDefType("edismax");
        query.setDefaultOperator(Query.Operator.AND);

        // 2. 设置分组查询
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        // 3. 添加关键字条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        // 4. 执行分组查询，获取分组结果
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");

        // 5. 获取商品分类后，封装，返回
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            categoryList.add(groupValue);
        }

        return categoryList;
    }

    /**
     * 功能：组合条件搜索，并将结果高亮显示
     * 参数：Map类型数据
     * 返回：高亮的搜索结果
     */
    private Map<String, Object> searchHighlightItemList(Map searchMap) {
        // 返回结果
        Map<String, Object> map = new HashMap<>();
        // 创建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();

        // 设置分词后的结果为与的关系
        // edismax查询解析器支持将多个单词增强为连续的单词对
        query.setDefType("edismax");
        query.setDefaultOperator(Query.Operator.AND);

        // 1. 设置高亮显示
        HighlightOptions highlightOption = new HighlightOptions();
        highlightOption.addField("item_title");
        highlightOption.setSimplePrefix("<em style='color:blue'><b>");
        highlightOption.setSimplePostfix("</b></em>");
        query.setHighlightOptions(highlightOption);

        // 2. 添加关键字条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        // 6. 添加商品分类过滤条件
        String category = (String) searchMap.get("category");
        if (StringUtils.isNotEmpty(category)) {
            FilterQuery categoryQuery = new SimpleFilterQuery();
            Criteria item_category = new Criteria("item_category").is(category);
            categoryQuery.addCriteria(item_category);
            query.addFilterQuery(categoryQuery);
        }

        // 7. 添加品牌过滤条件
        String brand = (String) searchMap.get("brand");
        if (StringUtils.isNotEmpty(brand)) {
            FilterQuery brandQuery = new SimpleFilterQuery();
            Criteria item_brand = new Criteria("item_brand").is(brand);
            brandQuery.addCriteria(item_brand);
            query.addFilterQuery(brandQuery);
        }

        // 8. 添加规格过滤条件
        if (searchMap.get("spec") != null) {
            Map<String, String> spec = (Map<String, String>) searchMap.get("spec");
            for (String key : spec.keySet()) {
                FilterQuery specQuery = new SimpleFilterQuery();
                Criteria item_spec = new Criteria("item_spec_" + key).is(spec.get(key));
                specQuery.addCriteria(item_spec);
                query.addFilterQuery(specQuery);
            }
        }

        // 11. 添加价格区间过滤条件
        if (!"".equals(searchMap.get("price"))) {
            String priceStr = (String) searchMap.get("price");
            String[] split = priceStr.split("-");
            // 如果最低价格不是0，则添加最低价格
            if (!"0".equals(split[0])) {
                FilterQuery priceQuery = new SimpleFilterQuery();
                Criteria item_price = new Criteria("item_price").greaterThanEqual(split[0]);
                priceQuery.addCriteria(item_price);
                query.addFilterQuery(priceQuery);
            }
            // 如果最高价格不是*，则添加最高价格
            if (!"*".equals(split[1])) {
                FilterQuery priceQuery = new SimpleFilterQuery();
                Criteria item_price = new Criteria("item_price").lessThanEqual(split[1]);
                priceQuery.addCriteria(item_price);
                query.addFilterQuery(priceQuery);
            }
        }

        // 9. 添加分页条件
        int pageNo = (int) searchMap.get("pageNo");
        int pageSize = (int) searchMap.get("pageSize");
        query.setOffset((pageNo - 1) * pageSize);
        query.setRows(pageSize);

        // 12. 添加排序条件
        String sortField = (String) searchMap.get("sortField");
        String sortMethod = (String) searchMap.get("sortMethod");
        if (StringUtils.isNotEmpty(sortField)) {
            if ("DESC".equals(sortMethod)) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
            if ("ASC".equals(sortMethod)) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        // 3. 执行高亮查询，获取查询结果
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        // 4. 遍历结果，替换高亮的域
        List<HighlightEntry<TbItem>> highlightEntries = highlightPage.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlightEntries) {
            // 4.1 获取原实体类
            TbItem entity = highlightEntry.getEntity();
            // 4.2 获取高亮字段
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            // 4.3 如果查询到结果，则进行高亮替换
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0) {
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }

        // 10. 获取分页结果
        map.put("totalPages", highlightPage.getTotalPages());
        map.put("totalItems", highlightPage.getTotalElements());

        // 5. 封装查询结果，返回
        map.put("rows", highlightPage.getContent());
        return map;
    }
}
