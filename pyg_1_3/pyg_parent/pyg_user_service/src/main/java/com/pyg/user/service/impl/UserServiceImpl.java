package com.pyg.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pyg.mapper.TbUserMapper;
import com.pyg.pojo.TbUser;
import com.pyg.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbUserMapper userMapper;
    @Value("#{jmsTemplate}")
    private JmsTemplate jmsTemplate;
    @Value("#{smsCodeDestination}")
    private Destination smsCodeDestination;

    @Override
    public void add(TbUser user) {
        // 加密密码
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        // 添加注册和修改日期
        user.setCreated(new Date());
        user.setUpdated(new Date());
        userMapper.insert(user);
    }

    @Override
    public void sendCode(String phone) {
        // 生成验证码
        String checkCode = RandomStringUtils.randomNumeric(6);
        // 保存在redis
        redisTemplate.boundHashOps("smsCode").put(phone, checkCode);
        // 使用activemq发送消息
        jmsTemplate.send(smsCodeDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phoneNumbers", phone);
                mapMessage.setString("signName", "杨伟峰");
                mapMessage.setString("templateCode", "SMS_140380046");
                Map param = new HashMap();
                param.put("code", checkCode);
                String paramStr = JSON.toJSONString(param);
                mapMessage.setString("templateParam", paramStr);
                return mapMessage;
            }
        });

    }

}
