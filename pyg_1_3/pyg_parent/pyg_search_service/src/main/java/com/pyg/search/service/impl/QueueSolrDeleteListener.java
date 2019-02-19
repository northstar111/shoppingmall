package com.pyg.search.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 作者：杨立波  时间：2018-09-16 20:22
 * 项目：pyg_parent
 * 说明：队列模式的删除索引监听器
 */
@Component
public class QueueSolrDeleteListener implements MessageListener {

    @Value("#{solrTemplate}")
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        // 接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;
        // json转换
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            // 1. 构造查询条件
            SolrDataQuery query = new SimpleQuery();
            Criteria item_goodsId = new Criteria("item_goodsId").in(goodsIds);
            query.addCriteria(item_goodsId);

            // 2. 执行删除
            solrTemplate.delete(query);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
