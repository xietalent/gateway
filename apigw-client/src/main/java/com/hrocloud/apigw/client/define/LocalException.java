package com.hrocloud.apigw.client.define;

public class LocalException extends Exception {

    private String             data;

    private AbstractReturnCode code;

    public LocalException(AbstractReturnCode code, String data) {
        this.code = code;
        this.data = data;
    }
    public String getData() {
        return data;
    }
    public AbstractReturnCode getCode() {
        return code;
    }
}
