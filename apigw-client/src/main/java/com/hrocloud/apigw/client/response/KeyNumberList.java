package com.hrocloud.apigw.client.response;

import com.hrocloud.apigw.client.annoation.Description;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 14-5-10.
 */
@Description("键,整形值列表")
public class KeyNumberList implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("键,整形值列表")
    public List<KeyNumberPair> keyValue;
}