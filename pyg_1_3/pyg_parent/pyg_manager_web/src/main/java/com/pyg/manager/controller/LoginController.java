package com.pyg.manager.controller;

/**
 * 作者：杨立波  时间：2018/9/3 19:50
 */

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 从security中获取用户登陆信息
 */
@RestController
public class LoginController {

    @RequestMapping("loginName")
    public Map loginName() {
        Map map = new HashMap();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName", username);
        return map;
    }
}
