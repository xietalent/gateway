package com.hrocloud.apigw.client.dubboext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hrocloud.apigw.client.define.ConstField;
import com.hrocloud.apigw.client.define.AbstractReturnCode;

public class DubboExtProperty {

    private static       Logger                           logger               = LoggerFactory.getLogger(DubboExtProperty.class);

    private static final String                           JSONOBJECT_SEPARATOR = ",";

    public static final  String                           LOG_SPLITTER         = new String(new char[]{' ', 2});

    final static         ThreadLocal<Map<String, String>> notifications        = new ThreadLocal<Map<String, String>>();

    static void addNotifications(String key, String value) {
        Map<String, String> map = notifications.get();
        if (map == null) {
            map = new HashMap<String, String>();
            notifications.set(map);
        }
        map.put(key, value);
    }

    static void addNotifications(Map<String, String> rpcMap) {
        if (rpcMap != null && !rpcMap.isEmpty()) {
            Map<String, String> map = notifications.get();
            if (map == null) {
                map = new HashMap<String, String>();
                notifications.set(map);
            }
            for (Entry<String, String> entry : rpcMap.entrySet()) {
                if (ConstField.ERROR_CODE.equals(entry.getKey())) {
                    String currentErrorCode = map.get(entry.getKey());
                    if (currentErrorCode != null && !currentErrorCode.isEmpty()) {
                        logger.info(
                                "provider return an error code ,but current service has set an error code.current error code:{},return error code:{}",
                                currentErrorCode, entry.getValue());
                    } else {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }  else {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    public static Map<String, String> getCurrentNotifications() {
        return notifications.get();
    }

    static String getValue(String key) {
        Map<String, String> map = notifications.get();
        return map == null ? null : map.get(key);
    }

    static boolean containsKey(String key) {
        Map<String, String> map = notifications.get();
        return map == null ? false : map.containsKey(key);
    }

    public static void setErrorCode(AbstractReturnCode code) {
        if (code != null) {
            if (!containsKey(ConstField.ERROR_CODE)) {
                addNotifications(ConstField.ERROR_CODE, String.valueOf(code.getCode()));
            }
        }
    }

    public static String getErrorCode() {
        return getValue(ConstField.ERROR_CODE);
    }

    public static void setCookieToken(String token) {
        if (token != null) {
            addNotifications(ConstField.SET_COOKIE_TOKEN, token);
        }
    }

    public static String getCookieToken() {
        return getValue(ConstField.SET_COOKIE_TOKEN);
    }



    public static String getMessageInfo() {
        return getValue(ConstField.MSG);
    }

    public static void clearNotificaitons() {
        Map map = notifications.get();
        if (map != null && !map.isEmpty()) {
            map.clear();
        }
    }


}
