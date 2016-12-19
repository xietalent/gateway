package com.hrocloud.apigw.client.response;

import com.hrocloud.apigw.client.annoation.Description;

import java.io.Serializable;

@Description("键整形值对")
public class KeyNumberPair implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("键")
    public String key;
    @Description("整形值")
    public int    value;

    public KeyNumberPair() {}

    public KeyNumberPair(String key, int value) {
        this.key = key;
        this.value = value;
    }
}