package com.hrocloud.apigw.utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.hrocloud.apigw.client.define.SerializeType;
import com.hrocloud.apigw.client.define.LocalException;
import com.hrocloud.apigw.client.utils.KeyValuePair;
import com.hrocloud.apigw.client.define.CallerInfo;
import org.slf4j.MDC;

public class ApiContext {

    private static ThreadLocal<ApiContext> threadLocal = new ThreadLocal<ApiContext>();

    public static ApiContext getCurrent() {

        ApiContext current = threadLocal.get();

        if (current == null) {
            current = new ApiContext();
            threadLocal.set(current);
        }

        return current;
    }

    private ApiContext() {
    }

    public String dubboAppid;

    public ArrayList<ApiMethodCall> apiCallInfos = null;

    public ApiMethodCall currentCall = null;

    public String requestInfo;

    public String uid;

    public String cid;

    public String deviceId;

    public long devId;

    public int appid;

    public SerializeType format = SerializeType.JSON;

    public long startTime = 0;

    public String agent;

    public String clientIP;

    public String token;

    public int requiredSecurity;

    public CallerInfo caller;

    public int serializeCount;

    public LocalException localException;

    public boolean fatalError = false;


    public ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);


    private Map<String, KeyValuePair> notifications = null;

    public final void addNotification(KeyValuePair n) {
        if (n == null) return;
        if (notifications == null) {
            notifications = new HashMap<String, KeyValuePair>();
        }
        if (!notifications.containsKey(n.key)) {
            notifications.put(n.key, n);
        }
    }

    public final void clearNotification() {
        if (notifications != null) {
            notifications.clear();
        }
    }

    public final List<KeyValuePair> getNotifications() {
        if (notifications != null) {
            return new ArrayList<KeyValuePair>(notifications.values());
        }
        return null;
    }

    public final void clear() {
    	this.dubboAppid = null;
        this.agent = null;
        this.apiCallInfos = null;
        this.appid = 0;
        this.caller = null;
        this.cid = null;
        this.clientIP = null;
        this.currentCall = null;
        this.deviceId = null;
        this.devId =  0;
        this.format = SerializeType.JSON;
        this.requestInfo = null;
        this.serializeCount = 0;
        this.startTime = 0;
        this.token = null;
        this.uid = null;
        this.outputStream.reset();
        this.notifications = null;
        this.requiredSecurity = 0;
        this.fatalError = false;
        MDC.clear();
    }
}