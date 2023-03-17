package com.zbh.gatewayroute.controller;

import com.zbh.gatewayroute.component.RedisUtils;
import com.zbh.gatewayroute.constant.UserConstant;
import com.zbh.gatewayroute.entity.Users;
import com.zbh.gatewayroute.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/user")
@RefreshScope
public class ConfigController {

    //todo
    //1一个url取消全部用户授权 都去访问最新的服务
    // 删除redis中的用户并且把所有的用户都添加进去
    //2一个url给所有用户授权 都去访问旧版服务
    // 删除redis中所有的用户
    //3.勾选新用户 添加到redis中

    @Autowired
    IUsersService usersService;

    @Autowired
    RedisUtils redisUtils;

    @RequestMapping("/getAll")
    @ResponseBody
    public List<Users> getAll(){
        List<Users> all = usersService.getAll();
        redisUtils.set("users",all);
        return all;
    }

    @RequestMapping("/getFromRedis")
    @ResponseBody
    public String getFromRedis(){
        return redisUtils.get("users");
    }


    /**
     * 把所有用户切换到灰度版本
     * @return
     */
    @RequestMapping("/routeAll")
    public String routeAll(){

        //先删除所有的用户信息
        redisUtils.delete(UserConstant.USER_KEY);
        //再把所有的用户信息添加进去
        redisUtils.set(UserConstant.USER_KEY,usersService.getAll());

        return "切换到新版本成功!";
    }


    /**
     * 把所有用户切换到生产版本
     * @return
     */
    @RequestMapping("/removeAll")
    public String removeAll(){
        //删除所有的用户信息
        redisUtils.delete(UserConstant.USER_KEY);
        return "切换到旧版信息成功";
    }
}
