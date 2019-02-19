package com.pyg.solr.util;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 作者：杨立波  时间：2018-09-13 19:45
 * 项目：pyg_parent
 * 说明：将数据导入到solr索引库
 */
@Component
public class ImportIntoSolr {

    @Value("#{tbItemMapper}")
    private TbItemMapper tbItemMapper;
    @Value("#{solrTemplate}")
    private SolrTemplate solrTemplate;

    public static void main(String[] args) {
        // 1. 创建导入对象
        ClassPathXmlApplicationContext xmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext_*.xml");
        ImportIntoSolr importIntoSolr = (ImportIntoSolr) xmlApplicationContext.getBean("importIntoSolr");

        // 2. 执行导入
        importIntoSolr.importData();
    }

    /**
     * 功能：将商品SKU数据导入索引库
     * 参数：无
     * 返回：无
     */
    private void importData() {
        // 1. 从数据库获取商品SKU数据
        // 要求：审核通过的SKU
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);

        // 2 替换商品规格字段
        for (TbItem tbItem : tbItems) {
            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);
        }

        // 3. 将数据导入solr索引库
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }
}
