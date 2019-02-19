package com.pyg.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pyg.pojo.TbItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * 作者：杨立波  时间：2018-09-16 19:56
 * 项目：pyg_parent
 * 说明：队列模式的添加solr索引监听器
 */
@Component
public class QueueSolrAddListener implements MessageListener {

    @Value("#{solrTemplate}")
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        // 接收消息
        TextMessage textMessage = (TextMessage) message;
        // json转换
        try {
            List<TbItem> itemList = JSON.parseArray(textMessage.getText(), TbItem.class);
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
