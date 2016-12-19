package com.hrocloud.apigw.client.utils;

import java.io.Serializable;

public class KeyValuePair implements Serializable {
    private static final long serialVersionUID = 1L;
    public String key;
    public String value;

    public KeyValuePair() {}

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
