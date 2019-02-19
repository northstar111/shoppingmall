package com.pyg.page.service;

/**
 * 作者：杨立波  时间：2018-09-15 10:59
 * 项目：pyg_parent
 * 说明：静态页面服务接口
 */
public interface ItemPageService {

    /**
     * 根据传递的SPU商品id，生成静态页面
     *
     * @param goodsId 商品id
     */
    public void genHtml(Long goodsId);
    
    /**
     * 根据传递的SPU商品id，删除相关静态页面
     *
     * @param goodsId 商品id
     */
    public void deleteHtml(Long goodsId);
}
