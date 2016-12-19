package com.hrocloud.apigw.exception;

/**
 * Created by hanzhihua on 2016/11/21.
 */
public class GatewayException extends Exception {

    private String tips;

    public GatewayException(String tips) {
        this.tips = tips;
    }

    public GatewayException(String tips, Throwable exception) {
        this.tips = tips;
        this.initCause(exception);
    }

    public String getTips() {
        return this.tips;
    }
}