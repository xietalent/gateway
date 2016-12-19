package com.hrocloud.apigw.client.response;

import com.hrocloud.apigw.client.annoation.Description;

import java.io.Serializable;

@Description("返回json格式的string")
public class JSONString implements Serializable {
    @Description("json string")
    public String value;
}