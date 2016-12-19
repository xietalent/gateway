package com.hrocloud.apigw.client.response;

import com.hrocloud.apigw.client.annoation.Description;

import java.io.Serializable;

@Description("长整形数组返回值")
public class LongArrayResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("长整形数组返回值")
    public long[] value;

    public static LongArrayResp convert(long[] ls) {
        LongArrayResp la = new LongArrayResp();
        la.value = ls;
        return la;
    }
}
