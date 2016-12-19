package com.hrocloud.apigw.client.define;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ReturnCodeContainer {
    private static HashMap<Integer, AbstractReturnCode> map = new HashMap<Integer, AbstractReturnCode>();

    static {
        Class returnCodeClass = ApiReturnCode.class;
        Class abstractReturnCodeClass = AbstractReturnCode.class;
        try {
            for (Field f : returnCodeClass.getDeclaredFields()) {
                if (isConstField(f) && AbstractReturnCode.class.isAssignableFrom(f.getType())) {
                    AbstractReturnCode code = (AbstractReturnCode)f.get(null);
                    code.setName(f.getName());
                    ReturnCodeContainer.putReturnCodeSuper2Map(code);
                }
            }
            for (Field f : abstractReturnCodeClass.getDeclaredFields()) {
                if (isConstField(f) && AbstractReturnCode.class.isAssignableFrom(f.getType())) {
                    AbstractReturnCode code = (AbstractReturnCode)f.get(null);
                    code.setName(f.getName());
                    ReturnCodeContainer.putReturnCodeSuper2Map(code);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("parse code failed. " + returnCodeClass.getName(), e);
        }
    }

    public static AbstractReturnCode findCode(int c) {
        AbstractReturnCode code = map.get(c);
        if (code == null) {
            code = ApiReturnCode.INTERNAL_SERVER_ERROR;
        }
        return code;
    }

    public static AbstractReturnCode getCode(int c) {
        return map.get(c);
    }

    public static void putReturnCodeSuper2Map(AbstractReturnCode abstractReturnCode) {
        AbstractReturnCode rc = map.get(abstractReturnCode.getCode());
        if (rc == null) {
            map.put(abstractReturnCode.getCode(), abstractReturnCode);
        }
    }

    public static boolean isConstField(Field field) {
        int efm = field.getModifiers();
        if (Modifier.isPublic(efm) && Modifier.isStatic(efm) && Modifier.isFinal(efm)) {
            return true;
        }
        return false;
    }

}
