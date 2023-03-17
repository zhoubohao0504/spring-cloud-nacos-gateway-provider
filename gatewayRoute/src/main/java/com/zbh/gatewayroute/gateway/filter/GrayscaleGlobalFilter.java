package com.zbh.gatewayroute.gateway.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbh.gatewayroute.component.EnvProperties;
import com.zbh.gatewayroute.component.GrayscaleThreadLocalEnvironment;
import com.zbh.gatewayroute.component.RedisUtils;
import com.zbh.gatewayroute.constant.UserConstant;
import com.zbh.gatewayroute.entity.Users;
import com.zbh.gatewayroute.service.IUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RefreshScope
@Slf4j
public class GrayscaleGlobalFilter implements GlobalFilter, Ordered {


    @Autowired
    EnvProperties envProperties;

    @Autowired
    IUsersService usersService;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        /*
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.add("Content-Type","application/json; charset=UTF-8");
        List<String> list = request.getHeaders().get("userId");

        if(Objects.isNull(list) || list.isEmpty()){
            return resultErrorMsg(response," 缺少userId");
        }

        String userId = list.get(0);
        if (StringUtils.isBlank(userId)) {
            return resultErrorMsg(response," 缺少userId！");
        }*/

        // todo 可以根据当前的用户信息设置不同响应内容 ，这里暂未处理
        Users randomUser = usersService.getRandomUser();

        log.info("当前登录用户信息: 用户民{}，用户id{},用户组{}" ,randomUser.getName(),randomUser.getId(),randomUser.getUserGroup());
        String usersStr =  redisUtils.get(UserConstant.USER_KEY);

        List<Users> usersList = new ArrayList<>();
        if(!Objects.isNull(usersStr) && JSONUtil.isJsonArray(usersStr)){
            usersList = JSONUtil.toList(JSONUtil.parseArray(usersStr), Users.class);
        }
        List<Integer> collect = usersList.stream().map(e -> e.getId()).collect(Collectors.toList());

        //针对不同的userId设置不同版本
        if (collect.contains(randomUser.getId())) {

            //指定灰度版本
            log.info("路由到灰度版本");
            GrayscaleThreadLocalEnvironment.setCurrentEnvironment(UserConstant.PROD_VERSION);


        } else {
            //指定生产版本
            log.info("路由到生产版本");
            GrayscaleThreadLocalEnvironment.setCurrentEnvironment(UserConstant.DEV_VERSION);
        }
        return chain.filter(exchange.mutate().request(request).build());
    }

    private Mono<Void> resultErrorMsg(ServerHttpResponse response, String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "403");
        jsonObject.put("message", msg);
        DataBuffer buffer = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
