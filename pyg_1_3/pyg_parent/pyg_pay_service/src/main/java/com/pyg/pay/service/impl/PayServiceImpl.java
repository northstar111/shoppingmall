package com.pyg.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.pay.service.PayService;
import com.pyg.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付服务实现类
 *
 * @author 杨立波  2018-09-22 17:00
 */

@Service
public class PayServiceImpl implements PayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;


    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        // 1.准备微信支付的请求参数
        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("mch_id", partner);
        params.put("nonce_str", WXPayUtil.generateNonceStr());
        params.put("body", "品优购商城");
        params.put("out_trade_no", out_trade_no);
        params.put("total_fee", total_fee);
        params.put("spbill_create_ip", "127.0.0.1");
        params.put("notify_url", "https://www.baidu.com/");
        params.put("trade_type", "NATIVE");

        try {
            // 2.生成签名后的xml格式参数单
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("支付参数：\n" + xmlParam);
            // 3.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            // 4.获取返回结果
            String content = httpClient.getContent();
            System.out.println("返回结果：\n" + content);
            // 5.将xml格式结果转为map格式
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            // 6.封装结果后返回
            Map<String, String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        // 1.创建查询订单的请求参数
        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("mch_id", partner);
        params.put("out_trade_no", out_trade_no);
        params.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
            // 2.生成签名后的xml格式参数单
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("查询参数：\n" + xmlParam);
            // 3.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            // 4.获取返回结果
            String content = httpClient.getContent();
            System.out.println("返回结果：\n" + content);
            // 5.将xml格式结果转为map格式后返回
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        // 1.创建查询订单的请求参数
        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("mch_id", partner);
        params.put("out_trade_no", out_trade_no);
        params.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
            // 2.生成签名后的xml格式参数单
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("查询参数：\n" + xmlParam);
            // 3.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            // 4.获取返回结果
            String content = httpClient.getContent();
            System.out.println("返回结果：\n" + content);
            // 5.将xml格式结果转为map格式后返回
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
