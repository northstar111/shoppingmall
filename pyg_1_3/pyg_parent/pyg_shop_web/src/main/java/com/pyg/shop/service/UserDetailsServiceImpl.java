package com.pyg.shop.service;

import com.pyg.pojo.TbSeller;
import com.pyg.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：杨立波  时间：2018/9/3 23:56
 */
/*
 * 实现商家登陆
 */
public class UserDetailsServiceImpl implements UserDetailsService{

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 获取用户登陆信息
        // security不拦截自己调用的请求
        TbSeller seller = sellerService.findOne(username);
        // 如果用户存在，返回用户登陆信息
        if (seller != null || !"0".equals(seller.getStatus())){
            // 获取权限
            List<GrantedAuthority> authorities = new ArrayList<>();
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_SELLER");
            authorities.add(authority);
            return new User(username, seller.getPassword(), authorities);
        }
        return null;
    }
}
