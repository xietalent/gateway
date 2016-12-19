package com.hrocloud.apigw.client.define;


import com.hrocloud.apigw.client.utils.KeyValuePair;

import java.util.List;

public class Response {

    public long systime;

    public int code;

    public String cid;

    public String data;

    public List<CallState> stateList;

    public List<KeyValuePair> notificationList;
}
