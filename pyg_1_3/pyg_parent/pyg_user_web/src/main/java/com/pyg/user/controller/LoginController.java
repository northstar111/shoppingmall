package com.pyg.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 从security中获取用户登陆信息
 */
@RestController
@RequestMapping("login")
public class LoginController {

    @RequestMapping("findLoginUser")
    public Map loginName(){
        Map map = new HashMap();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName", username);
        return map;
    }
}
