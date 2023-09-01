package com.zbh.gatewayroute.gateway.loadBalance;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import com.zbh.gatewayroute.component.GrayscaleThreadLocalEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class EnvRoundRobinRule extends RoundRobinRule {

    private AtomicInteger nextServerCyclicCounter;

    public EnvRoundRobinRule( ){
        this.nextServerCyclicCounter = new AtomicInteger(0);
    }

    @Override
    public Server choose(ILoadBalancer lb, Object key) {
        if(lb ==null){
            System.out.println("no load balancer");
            return null;
        }

        Server server = null;

        int count = 0;
        //失败重试10次
        while (Objects.isNull(server) && count++ <10){

            List<Server> reachableServers = lb.getReachableServers();
            List<Server> allServers = lb.getAllServers();
            int upCount = reachableServers.size();
            int serverCount = allServers.size();

            if(upCount==0 || serverCount ==0){
                System.out.println("No up servers available from load balancer: ");
                return null;
            }

            List<NacosServer> filterServers = new ArrayList<>();

            String currentEnvironmentVersion = GrayscaleThreadLocalEnvironment.getCurrentEnvironment();

            for (Server reachableServer : reachableServers) {
                NacosServer nacosServer = (NacosServer)reachableServer;

                String version = nacosServer.getMetadata().get("version");
                if(version.equals(currentEnvironmentVersion)){
                    filterServers.add(nacosServer);
                }
            }
            int filterServerCount = filterServers.size();
            int nextServerIndex = incrementAndGetModulo(filterServerCount);
            server = filterServers.get(nextServerIndex);


            if(server ==null){
                Thread.yield();
                continue;
            }
            if(server.isAlive() && server.isReadyToServe()){
                return server;
            }
            server = null;
        }
        if (count >= 10) {
            System.out.println("No available alive servers after 10 tries from load balancer: "
                    + lb);
        }
        return server;
    }

    private int incrementAndGetModulo(int modulo) {
        for (; ; ) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }
}
