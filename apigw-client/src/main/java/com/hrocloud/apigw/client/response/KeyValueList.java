package com.hrocloud.apigw.client.response;

import com.hrocloud.apigw.client.annoation.Description;

import java.io.Serializable;
import java.util.List;

@Description("键值对列表")
public class KeyValueList implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("键值对列表")
    public List<KeyValuePair> keyValue;
}
