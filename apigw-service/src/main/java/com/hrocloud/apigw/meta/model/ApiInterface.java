package com.hrocloud.apigw.meta.model;

import java.io.Serializable;

public class ApiInterface implements Serializable {
    private static final long serialVersionUID = 1L;

    private String invokeInterface;

    public String getInvokeInterface() {
        return invokeInterface;
    }

    public void setInvokeInterface(String invokeInterface) {
        this.invokeInterface = invokeInterface;
    }
}
