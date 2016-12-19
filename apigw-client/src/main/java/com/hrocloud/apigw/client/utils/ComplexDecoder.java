package com.hrocloud.apigw.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class ComplexDecoder {

    private String data;

    private Object nativeObj4Json;

    public ComplexDecoder(String data) {
        this.data = data;
    }

    public boolean decode() {

        Object object =  JSON.parse(data);
        nativeObj4Json = jsonToNative(object);
        return true;
    }

    public Object getNativeObj4Json() {
        return nativeObj4Json;
    }

    private static Object jsonObjToArray(JSONArray jsonArray){
        List ret = new ArrayList();
        Iterator it = jsonArray.iterator();
        while(it.hasNext()){
            Object jsonObject = it.next();
            if (jsonObject instanceof JSONObject) {
                ret.add(jsonObjToMap((JSONObject) jsonObject));
            }
            else if (jsonObject instanceof JSONArray) {
                ret.add(jsonObjToArray((JSONArray) jsonObject));
            }
            else{
                ret.add(jsonObject);
            }
        }

        return ret;
    }

    private static Object jsonObjToMap(JSONObject jsonObject){
        Map ret = new HashMap();

        for (Map.Entry entry : jsonObject.entrySet()) {
            if (entry.getValue() instanceof JSONObject) {
                ret.put(entry.getKey(), jsonObjToMap((JSONObject) entry.getValue()));
            }
            else if (entry.getValue() instanceof JSONArray) {
                ret.put(entry.getKey(), jsonObjToArray((JSONArray) entry.getValue()));
            }
            else{
                ret.put(entry.getKey(), entry.getValue());
            }
        }
        return ret;
    }

    private static Object jsonToNative(Object object){
        Object ret = null;
        if(object instanceof JSONObject) {
            ret = jsonObjToMap((JSONObject) object);
        }
        else if(object instanceof JSONArray) {
            ret = jsonObjToArray((JSONArray) object);
        }
        else {
            ret = object;
        }

        return ret;
    }

}
