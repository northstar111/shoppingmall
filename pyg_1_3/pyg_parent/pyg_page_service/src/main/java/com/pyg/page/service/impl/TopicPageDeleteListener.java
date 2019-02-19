package com.pyg.page.service.impl;

import com.pyg.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 作者：杨立波  时间：2018-09-16 23:40
 * 项目：pyg_parent
 * 说明：主题模式的删除页面监听器
 */
@Component
public class TopicPageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        // 接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;
        // 执行业务操作
        try {
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            for (Long goodsId : goodsIds) {
                itemPageService.deleteHtml(goodsId);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
