package com.hrocloud.apigw;

/**
 * Created by hanzhihua on 2016/11/15.
 */
public class ApigwServer {

    public static void main(String[] args) {
        new SecurityInit().init();
        com.alibaba.dubbo.container.Main.main(args);
    }
}
