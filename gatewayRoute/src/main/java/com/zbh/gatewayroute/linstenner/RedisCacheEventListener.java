package com.zbh.gatewayroute.linstenner;

import com.zbh.gatewayroute.component.RedisUtils;
import com.zbh.gatewayroute.constant.UserConstant;
import com.zbh.gatewayroute.service.IUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RedisCacheEventListener {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    IUsersService usersService;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event){

        redisUtils.set(UserConstant.USER_KEY,usersService.getTopGroupUser());
        log.info("redis用户已缓存" + redisUtils.get(UserConstant.USER_KEY));
    }
}
