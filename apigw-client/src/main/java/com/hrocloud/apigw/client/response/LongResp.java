package com.hrocloud.apigw.client.response;

import com.hrocloud.apigw.client.annoation.Description;

import java.io.Serializable;

@Description("长整形返回值")
public class LongResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("长整形返回值")
    public long value;

    public static LongResp convert(long l) {
        LongResp lr = new LongResp();
        lr.value = l;
        return lr;
    }
}
