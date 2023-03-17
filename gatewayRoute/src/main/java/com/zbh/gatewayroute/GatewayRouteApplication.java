package com.zbh.gatewayroute;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zbh.gatewayroute.dao")
public class GatewayRouteApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayRouteApplication.class, args);
    }

}
