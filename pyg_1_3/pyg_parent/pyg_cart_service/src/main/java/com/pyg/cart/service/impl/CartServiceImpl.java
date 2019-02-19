package com.pyg.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.cart.service.CartService;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现类
 *
 * @author 杨立波  2018-09-20 14:57
 */

@Service
public class CartServiceImpl implements CartService {

    @Value("#{tbItemMapper}")
    private TbItemMapper tbItemMapper;
    @Value("#{redisTemplate}")
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer itemNum) {
        // 1.查询SKU商品信息
        // 1.1获取商品
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        // 1.2检查商品状态
        if (tbItem == null) {
            throw new RuntimeException("该商品不存在");
        }
        if (!"1".equals(tbItem.getStatus())) {
            throw new RuntimeException("该商品状态无效");
        }

        // 2.判断购物车列表中是否存在商家的购物车
        // 2.1获取商家id
        String sellerId = tbItem.getSellerId();
        // 2.2判断是否存在
        Cart cart = searchCartListBySellerId(cartList, sellerId);

        // 3.如果不存在
        if (cart == null) {
            // 3.1新建该商家的购物车
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            // 3.2新建订单明细
            TbOrderItem tbOrderItem = createOrderItem(tbItem, itemNum);
            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(tbOrderItem);
            cart.setOrderItemList(orderItemList);
            // 3.3将该商家的购物车放入购物车列表中
            cartList.add(cart);
        } else {
            // 4.如果存在，再判断该商品是否已包含在订单明细列表中
            TbOrderItem tbOrderItem = searchOrderItemListByItemId(cart.getOrderItemList(), tbItem.getId());
            // 5.如果不包含
            if (tbOrderItem == null) {
                // 5.1新建订单明细
                tbOrderItem = createOrderItem(tbItem, itemNum);
                // 5.1放入列表中
                cart.getOrderItemList().add(tbOrderItem);
            } else {
                // 6.如果包含
                // 6.1修改该商品的总数量
                tbOrderItem.setNum(tbOrderItem.getNum() + itemNum);
                // 6.2修改该明细的总金额
                tbOrderItem.setTotalFee(tbOrderItem.getTotalFee().add(tbItem.getPrice().multiply(new BigDecimal(itemNum))));
                // 用户可能会删除购物车，所以需要判断
                // 6.3如果商品总数小于等于零，则移除该商品明细
                if (tbOrderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(tbOrderItem);
                }
                // 6.4如果订单明细小于等于零，则移除该购物车
                if (cart.getOrderItemList().size() <= 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        // 从redis中获取购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        // 检查该用户是否已存购物车
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        // 替换为新的cartList
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_cookie, List<Cart> cartList_redis) {
        for (Cart cart : cartList_cookie) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList_redis = addGoodsToCartList(cartList_redis, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList_redis;

    }

    /**
     * 检查订单明细列表中是否包含指定商品的明细
     *
     * @param orderItemList 订单明细列表
     * @param ItemId        商品id
     * @return 如果包含，返回该明细；如果不包含，返回Null
     */
    private TbOrderItem searchOrderItemListByItemId(List<TbOrderItem> orderItemList, Long ItemId) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (tbOrderItem.getItemId().equals(ItemId)) {
                return tbOrderItem;
            }
        }
        return null;
    }

    /**
     * 创建订单明细，将SKU商品信息填进明细中
     *
     * @param tbItem  SKU商品
     * @param itemNum 商品数量
     * @return 订单
     */
    private TbOrderItem createOrderItem(TbItem tbItem, Integer itemNum) {
        // 新建明细时，商品数量不能为负数
        if (itemNum <= 0) {
            throw new RuntimeException("商品数量非法");
        }
        // 填写明细
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setItemId(tbItem.getId());
        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setPrice(tbItem.getPrice());
        tbOrderItem.setNum(itemNum);
        // 大十进制乘法
        tbOrderItem.setTotalFee(tbItem.getPrice().multiply(new BigDecimal(itemNum)));
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setSellerId(tbItem.getSellerId());
        return tbOrderItem;
    }

    /**
     * 检查购物车列表中是否包含指定商家的购物车
     *
     * @param cartList 购物车列表
     * @param sellerId 商家id
     * @return 如果存在，返回该商家的购物车；如果不存在，返回null
     */
    private Cart searchCartListBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }
}
