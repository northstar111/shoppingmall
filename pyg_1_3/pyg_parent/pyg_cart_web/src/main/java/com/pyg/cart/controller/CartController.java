package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pyg.cart.service.CartService;
import com.pyg.pojogroup.Cart;
import com.pyg.util.CookieUtil;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 购物车控制类
 *
 * @author 杨立波  2018-09-20 16:30
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 获取购物车列表
     * 如果用户未登录，则从本地cookie中获取
     * 如果用户登录，则从redis中获取
     *
     * @return 购物车列表
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        // 1.获取用户的登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2.从cookie中获取购物车列表
        // 注意：该操作不会区分本地cookie是否属于同一用户
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (StringUtils.isEmpty(cartListStr)) {
            // 空字符串时json转换会出错
            cartListStr = "[]";
        }
        // json转换
        List<Cart> cartList_cookie = JSON.parseArray(cartListStr, Cart.class);
        // 3.判断用户是否登录
        // 3.1如果用户未登录，返回cookie中的购物车列表
        if ("anonymousUser".equals(username)) {
            return cartList_cookie;
        } else {
            // 3.2.如果用户登录
            // 3.2.1从redis中获取购物车列表
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            // 3.2.2如果本地cookie存在购物车列表，则将其合并到redis中，合并后清空cookie
            if (cartList_cookie.size()>0){
                cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
                cartService.saveCartListToRedis(username, cartList_redis);
                CookieUtil.deleteCookie(request, response, "cartList");
            }
            // 3.2.3返回redis中的购物车列表
            return cartList_redis;
        }
    }

    /**
     * 将商品保存到购物车列表后，
     * 如果用户未登录，保存到cookie中
     * 如果用户登录，保存到redis中
     * @CrossOrigin： 注解方式设置允许跨域请求，要求springMVC的版本在4.2或以上，默认允许带cookie
     *
     * @param itemId  sku商品id
     * @param itemNum 商品数量
     * @return 操作结果
     */
    @CrossOrigin(origins = "http://localhost:9105")
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer itemNum) {
        // 设置允许跨域访问，允许携带cookie
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 1.获取用户的登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            // 2.添加商品至购物车列表
            List<Cart> cartList = cartService.addGoodsToCartList(findCartList(), itemId, itemNum);
            // 3.判断用户是否登录
            // 3.1如果用户未登录，则将购物车列表保存到cookie中
            if ("anonymousUser".equals(username)) {
                // 替换为新的cartList
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
            } else {
                // 3.1如果用户登录，则将购物车列表保存到redis中
                cartService.saveCartListToRedis(username, cartList);
            }
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}
