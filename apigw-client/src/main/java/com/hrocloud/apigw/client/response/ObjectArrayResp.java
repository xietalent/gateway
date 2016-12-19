package com.hrocloud.apigw.client.response;

import com.hrocloud.apigw.client.annoation.Description;

import java.io.Serializable;
import java.util.Collection;


@Description("对象数组返回值")
public class ObjectArrayResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("对象数组返回值")
    public Object[] value;

    public static ObjectArrayResp convert(Object[] array) {
        ObjectArrayResp arrayResp = new ObjectArrayResp();
        arrayResp.value = array;
        return arrayResp;
    }

    public static ObjectArrayResp convert(Collection collection) {
        ObjectArrayResp arrayResp = new ObjectArrayResp();
        if (collection != null && collection.size() > 0) {
            arrayResp.value = collection.toArray();
        }
        return arrayResp;
    }
}
