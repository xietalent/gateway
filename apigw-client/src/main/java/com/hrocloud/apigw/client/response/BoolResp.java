package com.hrocloud.apigw.client.response;

import com.hrocloud.apigw.client.annoation.Description;

import java.io.Serializable;

@Description("布尔类型返回值")
public class BoolResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("布尔类型返回值")
    public boolean value;

    public static BoolResp convert(boolean b) {
        BoolResp br = new BoolResp();
        br.value = b;
        return br;
    }
}
