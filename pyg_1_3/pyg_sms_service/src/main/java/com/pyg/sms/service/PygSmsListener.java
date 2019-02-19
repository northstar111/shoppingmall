package com.pyg.sms.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.pyg.util.SmsUtil;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 品优购项目发送短信服务的消息监听器
 * 接收到消息后，立即发送短信，并且打印发送的内容
 *
 * @author 杨立波  2018-09-18 16:26
 */
@Component
public class PygSmsListener {

    @JmsListener(destination = "pyg_sms")
    public void sendSms(Map smsMap){
        try {
            SendSmsResponse response = SmsUtil.sendSms(smsMap);
            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
