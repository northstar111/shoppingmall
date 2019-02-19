package com.pyg.pay.service;

import java.util.Map;

/**
 * 支付服务接口
 *
 * @author 杨立波  2018-09-22 16:56
 */

public interface PayService {

    /**
     * 调用微信支付api，生成微信扫码支付url
     *
     * @param out_trade_no 商户订单号
     * @param total_fee 标价金额
     * @return Map
     */
    public Map createNative(String out_trade_no, String total_fee);

    /**
     * 查询订单支付状态
     *
     * @param out_trade_no 商户订单号
     * @return Map
     */
    public Map queryPayStatus(String out_trade_no);

    /**
     * 关闭支付
     *
     * @param out_trade_no 商户订单号
     * @return Map
     */
    public Map closePay(String out_trade_no);
}
