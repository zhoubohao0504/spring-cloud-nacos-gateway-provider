package com.zbh.gatewayroute.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@RefreshScope
@Configuration
public class EnvProperties {

    @Value("${env.prod.version}")
    private String proVersion;

    @Value("${env.dev.users}")
    private List<String> grayUsers;


    @Value("${env.dev.version}")
    private String grayVersion;

    public String getProVersion() {
        return proVersion;
    }

    public void setProVersion(String proVersion) {
        this.proVersion = proVersion;
    }

    public List<String> getGrayUsers() {
        return grayUsers;
    }

    public void setGrayUsers(List<String> grayUsers) {
        this.grayUsers = grayUsers;
    }

    public String getGrayVersion() {
        return grayVersion;
    }

    public void setGrayVersion(String grayVersion) {
        this.grayVersion = grayVersion;
    }
}
