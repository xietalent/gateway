package com.hrocloud.apigw.client.define;

public class SerializeException extends Exception {
    private Exception exception;
    public SerializeException(Exception exception) {
        this.exception = exception;
    }
    public Exception getException() {
        return exception;
    }
}
