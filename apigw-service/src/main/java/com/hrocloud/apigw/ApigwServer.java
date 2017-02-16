package com.hrocloud.apigw;

/**
 * Created by hanzhihua on 2016/11/15.
 */
public class ApigwServer {

    public static void main(String[] args) {
        System.getProperties().list(System.out);
        System.out.println("new SecurityInit().init()");
        System.out.println(ApigwServer.class.getClassLoader().getResource("javax/crypto/KeyGenerator.class"));

        new SecurityInit().init();
        com.alibaba.dubbo.container.Main.main(args);
    }
}
