package com.pyg.pojogroup;

/**
 * 作者：杨立波  时间：2018/9/4 11:07
 */

import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * 商品的组合实体类
 */
public class Goods implements Serializable{

    private TbGoods goods; // SPU基本信息
    private TbGoodsDesc goodsDesc; //SPU扩展信息
    private List<TbItem> itemList; //SKU详情信息

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
