package com.pyg.solr.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

/**
 * 作者：杨立波  时间：2018-09-13 21:14
 * 项目：pyg_parent
 * 说明：清空solr索引库
 */
@Component
public class EmptySolr {

    @Value("#{solrTemplate}")
    private SolrTemplate solrTemplate;

    public static void main(String[] args) {
        // 1. 创建删除对象
        ClassPathXmlApplicationContext xmlApplicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext_*.xml");
        EmptySolr emptySolr = (EmptySolr) xmlApplicationContext.getBean("emptySolr");

        // 2. 清空solr索引库
        emptySolr.emptySolr();
    }

    /**
     * 功能：清空solr索引库
     * 参数：无
     * 返回：无
     */
    private void emptySolr() {
        // 删除所有数据
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        // 一定要提交
        solrTemplate.commit();
    }
}
