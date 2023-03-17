package com.zbh.gatewayroute.component;

public class GrayscaleThreadLocalEnvironment {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setCurrentEnvironment(String currentEnvironment){
        threadLocal.set(currentEnvironment);
    }

    public static String getCurrentEnvironment(){
        return threadLocal.get();
    }

}
