package com.pyg.cart.service;

import com.pyg.pojogroup.Cart;

import java.util.List;

/**
 * 购物车服务接口
 *
 * @author 杨立波  2018-09-20 14:50
 */

public interface CartService {

    /**
     * 将商品添加到购物车中
     *
     * @param cartList 购物车列表
     * @param itemId SKU商品id
     * @param itemNum 商品数量
     * @return 购物车列表
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer itemNum);

    /**
     * 从redis中获取购物车列表
     *
     * @param username 用户登录名
     * @return 购物车列表
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车列表保存到redis中
     *
     * @param username 用户登录名
     * @param cartList 购物车列表
     */
    public void saveCartListToRedis(String username, List<Cart> cartList);

    /**
     * 当用户登录时，合并cookie和redis中的购物车列表信息
     * @param cartList_cookie cookie中的购物车列表
     * @param cartList_redis redis中的购物车列表
     * @return 合并后的购物车列表
     */
    public List<Cart> mergeCartList(List<Cart> cartList_cookie, List<Cart> cartList_redis);
}
