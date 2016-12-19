package com.hrocloud.apigw.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.TypeUtils;
import com.hrocloud.apigw.client.define.ApiReturnCode;
import com.hrocloud.apigw.client.define.ReturnCodeException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class MiscUtil {

    public static final String X_FORWARDED_FOR = "x-forwarded-for";

    public static final String HTTP_X_FORWARDED_FOR = "http-x-forwarded-for";

    public static final String REMOTE_ADDR = "remote-addr";

    public static final Comparator<String> StringComparator = new Comparator<String>() {

        @Override
        public int compare(String s1, String s2) {
            int n1 = s1 == null ? 0 : s1.length();
            int n2 = s2 == null ? 0 : s2.length();
            int mn = n1 < n2 ? n1 : n2;
            for (int i = 0; i < mn; i++) {
                int k = s1.charAt(i) - s2.charAt(i);
                if (k != 0) {
                    return k;
                }
            }
            return n1 - n2;
        }
    };

    public static String getClientIP(HttpServletRequest request) {
        String ip;
        ip = request.getHeader(X_FORWARDED_FOR);
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
            if (ip == null || ip.length() == 0) {
                ip = request.getHeader(REMOTE_ADDR);
                if (ip == null || ip.length() == 0) {
                    ip = request.getRemoteAddr();
                }
            }
        }
        return ip;
    }

    public static boolean isPrimitiveType(String type) {
        if ("java.lang.String".equals(type) || "boolean".equals(type) || "long".equals(type)
                || "int".equals(type) || "double".equals(type) || "float".equals(type)
                || "java.lang.Long".equals(type) || "short".equals(type) || "byte".equals(type)
                || "java.lang.Integer".equals(type) || "char".equals(type)
                || "java.lang.Float".equals(type) || "java.lang.Double".equals(type)
                || "java.lang.Short".equals(type) || "java.lang.Boolean".equals(type)
                || "java.lang.Byte".equals(type) || "java.lang.Character".equals(type)) {
            return true;
        }
        return false;
    }


    public static boolean isPrimitive(String className) {
        if (className != null) {
            if ("boolean".equals(className)) {
                return true;
            } else if ("long".equals(className)) {
                return true;
            } else if ("int".equals(className)) {
                return true;
            } else if ("double".equals(className)) {
                return true;
            } else if ("float".equals(className)) {
                return true;
            } else if ("short".equals(className)) {
                return true;
            } else if ("byte".equals(className)) {
                return true;
            } else if ("char".equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrimitiveArrayType(String type) {
        if ("java.lang.String[]".equals(type) || "boolean[]".equals(type) || "long[]".equals(type) || "int[]".equals(type)
                || "short[]".equals(type) || "double[]".equals(type) || "float[]".equals(type) || "byte[]".equals(type)
                || "char[]".equals(type)
                || "java.lang.Boolean[]".equals(type) || "java.lang.Long[]".equals(type) || "java.lang.Integer[]".equals(type)
                || "java.lang.Short[]".equals(type) || "java.lang.Double[]".equals(type) || "java.lang.Float[]".equals(type)
                || "java.lang.Byte[]".equals(type)
                || "java.lang.Character[]".equals(type)
                ) {
            return true;
        }
        return false;
    }

    public static String checkRevisePrimitiveArrayParam(String paramName, String oriStr, String paramType) {
        if (oriStr == null) {
            return null;
        }
        Map retMap = new HashMap();
        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(oriStr);
        } catch (Exception e) {
            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : param value is " + oriStr + ", but param type is " + paramType);
        }

        retMap.put(paramName, jsonArray);
        Map typeMap = new HashMap();
        typeMap.put(paramName, paramType);
        checkReviseRetMap(retMap, typeMap);
        return JSON.toJSONString(retMap.get(paramName));
    }

    public static void checkReviseRetMap(Map retMap, Map typeMap) {
        try {
            Iterator<Map.Entry<Object, Object>> it = retMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, Object> mapEntry = it.next();
                Object key = mapEntry.getKey();
                Object value = mapEntry.getValue();
                Object valueType = typeMap.get(key);//maybe get null
                if (valueType != null) {
                    if (isPrimitiveType(valueType.toString())) {
                        if ("java.lang.String".equals(valueType)) {
                            String stringValue = TypeUtils.castToString(value);
                            mapEntry.setValue(stringValue);
                        } else {
                            Object castValue = null;
                            if ("boolean".equals(valueType)) {
                                Boolean boolValue = TypeUtils.castToBoolean(value);
                                if (boolValue != null) {
                                    castValue = boolValue;
                                    if (boolValue.booleanValue()) {
                                        boolValue = Boolean.TRUE;
                                    } else {
                                        boolValue = Boolean.FALSE;
                                    }
                                    mapEntry.setValue(boolValue);
                                } else {
                                    castValue = Boolean.FALSE;
                                    mapEntry.setValue(Boolean.FALSE);
                                }

                            } else if ("long".equals(valueType) || "java.lang.Long".equals(valueType)) {
                                Long longValue = TypeUtils.castToLong(value);
                                if (longValue != null) {
                                    castValue = longValue;
                                    mapEntry.setValue(longValue);
                                } else {
                                    castValue = Long.valueOf(0);
                                    mapEntry.setValue(castValue);
                                }
                            } else if ("int".equals(valueType)) {
                                Integer intValue = TypeUtils.castToInt(value);
                                if (intValue != null) {
                                    castValue = intValue;
                                    mapEntry.setValue(intValue);
                                } else {
                                    castValue = Integer.valueOf(0);
                                    mapEntry.setValue(castValue);
                                }
                            } else if ("short".equals(valueType)) {
                                Short shortValue = TypeUtils.castToShort(value);
                                if (shortValue != null) {
                                    castValue = shortValue;
                                    mapEntry.setValue(shortValue);
                                }
                            } else if ("double".equals(valueType)) {
                                Double doubleValue = TypeUtils.castToDouble(value);
                                if (doubleValue != null) {
                                    castValue = doubleValue;
                                    mapEntry.setValue(doubleValue);
                                }
                            } else if ("float".equals(valueType)) {
                                Float floatValue = TypeUtils.castToFloat(value);
                                if (floatValue != null) {
                                    castValue = floatValue;
                                    mapEntry.setValue(floatValue);
                                }
                            } else if ("byte".equals(valueType)) {
                                Byte byteValue = TypeUtils.castToByte(value);
                                if (byteValue != null) {
                                    castValue = byteValue;
                                    mapEntry.setValue(byteValue);
                                }
                            } else if ("char".equals(valueType)) {
                                Character charValue = TypeUtils.castToChar(value);
                                if (charValue != null) {
                                    castValue = charValue;
                                    mapEntry.setValue(charValue);
                                }
                            }

                            if (castValue == null) {
                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                            }
                        }


                    } else if (valueType.toString().endsWith(Constants.ARRAY_TYPE_SUFFIX)) { //数组[]
                        if (value instanceof List) {
                            List listValue = (List) value;
                            if (MiscUtil.isPrimitiveArrayType(valueType.toString())) {
                                if ("java.lang.String[]".equals(valueType)) {
                                    for (int i = 0; i < listValue.size(); i++) {
                                        String stringValue = TypeUtils.castToString(listValue.get(i));
                                        listValue.set(i, stringValue);
                                    }
                                } else {
                                    if ("boolean[]".equals(valueType)) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Boolean boolValue = TypeUtils.castToBoolean(listValue.get(i));
                                            if (boolValue != null) {
                                                if (boolValue.booleanValue()) {
                                                    boolValue = Boolean.TRUE;
                                                } else {
                                                    boolValue = Boolean.FALSE;
                                                }
                                                listValue.set(i, boolValue);
                                            } else {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                                            }
                                        }
                                    } else if ("long[]".equals(valueType)) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Long longValue = TypeUtils.castToLong(listValue.get(i));
                                            if (longValue != null) {
                                                listValue.set(i, longValue);
                                            } else {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                                            }
                                        }
                                    } else if ("int[]".equals(valueType)) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Integer intValue = TypeUtils.castToInt(listValue.get(i));
                                            if (intValue != null) {
                                                listValue.set(i, intValue);
                                            } else {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                                            }
                                        }
                                    } else if ("short[]".equals(valueType)) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Short shortValue = TypeUtils.castToShort(listValue.get(i));
                                            if (shortValue != null) {
                                                listValue.set(i, shortValue);
                                            } else {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                                            }
                                        }
                                    } else if ("double[]".equals(valueType)) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Double doubleValue = TypeUtils.castToDouble(listValue.get(i));
                                            if (doubleValue != null) {
                                                listValue.set(i, doubleValue);
                                            } else {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                                            }
                                        }
                                    } else if ("float[]".equals(valueType)) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Float floatValue = TypeUtils.castToFloat(listValue.get(i));
                                            if (floatValue != null) {
                                                listValue.set(i, floatValue);
                                            } else {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                                            }
                                        }
                                    } else if ("byte[]".equals(valueType)) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Byte byteValue = TypeUtils.castToByte(listValue.get(i));
                                            if (byteValue != null) {
                                                listValue.set(i, byteValue);
                                            } else {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                                            }
                                        }
                                    } else if ("char[]".equals(valueType)) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Character charValue = TypeUtils.castToChar(listValue.get(i));
                                            if (charValue != null) {
                                                listValue.set(i, charValue);
                                            } else {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value);
                                            }
                                        }
                                    }

                                }

                            } else {
                                Object arrayTypeObj = typeMap.get(valueType);
                                String genericTypeName = valueType.toString().replace("[]", "");
                                if (!(arrayTypeObj instanceof Map)) {
                                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : Type Define in DB is wrong.");
                                } else {
                                    Object arrayGenericTypeObj = ((Map) arrayTypeObj).get(genericTypeName);
                                    if (arrayGenericTypeObj instanceof Map) {
                                        for (int i = 0; i < listValue.size(); i++) {
                                            Object aObj = listValue.get(i);
                                            if (!(aObj instanceof Map)) {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to {} failed");
                                            }
                                            checkReviseRetMap((Map) aObj, (Map) arrayGenericTypeObj);   //递归check........，
                                        }
                                    } else if (arrayGenericTypeObj instanceof List) {
                                        List enumList = new ArrayList();
                                        mapEntry.setValue(enumList);

                                        Iterator<Object> stuIter = listValue.iterator();
                                        while (stuIter.hasNext()) {
                                            Object aObj = stuIter.next();
                                            if (aObj instanceof Number) {
                                                int _index = TypeUtils.castToInt(aObj);
                                                if (((List) arrayGenericTypeObj).get(_index) != null) {
                                                    String enumName = ((List) arrayGenericTypeObj).get(_index).toString();
                                                    enumList.add(enumName);
                                                } else {
                                                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed, Enum Constants: " + arrayGenericTypeObj.toString());
                                                }

                                            } else if (aObj instanceof String) {
                                                if ("".equals(aObj)) {
                                                    stuIter.remove();
                                                } else if (!((List) arrayGenericTypeObj).contains(aObj)) {
                                                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed, Enum Constants: " + arrayGenericTypeObj.toString());
                                                } else {
                                                    enumList.add(aObj);
                                                }
                                            } else if (aObj != null) {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed, Enum Constants: " + arrayGenericTypeObj.toString());
                                            }
                                        }

                                    } else {
                                        throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : Type Define in DB is wrong.");
                                    }
                                }

                            }

                        } else {
                            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to [] failed");
                        }


                    } else if (valueType.toString().endsWith(">")) {
                        if (value instanceof List) {
                            List listValue = (List) value;
                            if ("java.util.List<java.lang.String>".equals(valueType)) {
                                for (int i = 0; i < listValue.size(); i++) {
                                    String stringValue = TypeUtils.castToString(listValue.get(i));
                                    listValue.set(i, stringValue);
                                }
                            } else {
                                Object listTypeObj = typeMap.get(valueType);
                                String oriGenericTypeName = valueType.toString();
                                String genericTypeName = oriGenericTypeName.substring(oriGenericTypeName.indexOf("<") + 1).replace(">", "");
                                if (!(listTypeObj instanceof Map)) {
                                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : Type Define in DB is wrong.");
                                } else {
                                    Object listGenericTypeObj = ((Map) listTypeObj).get(genericTypeName);
                                    if (listGenericTypeObj instanceof Map) {
                                        List objList = new ArrayList();
                                        mapEntry.setValue(objList);

                                        Iterator<Object> stuIter = listValue.iterator();
                                        while (stuIter.hasNext()) {
                                            Object aObj = stuIter.next();
                                            if (!(aObj instanceof Map) && aObj != null) {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to {} failed");
                                            }
                                            if (aObj != null) {
                                                objList.add(aObj);
                                                checkReviseRetMap((Map) aObj, (Map) listGenericTypeObj);   //递归check........，
                                            }
                                        }


                                    } else if (listGenericTypeObj instanceof List) {
                                        List enumList = new ArrayList();
                                        mapEntry.setValue(enumList);

                                        Iterator<Object> stuIter = listValue.iterator();
                                        while (stuIter.hasNext()) {
                                            Object aObj = stuIter.next();
                                            if (aObj instanceof Number) {
                                                int _index = TypeUtils.castToInt(aObj);
                                                if (((List) listGenericTypeObj).get(_index) != null) {
                                                    String enumName = ((List) listGenericTypeObj).get(_index).toString();
                                                    enumList.add(enumName);
                                                } else {
                                                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed, Enum Constants: " + listGenericTypeObj.toString());
                                                }

                                            } else if (aObj instanceof String) {
                                                if ("".equals(aObj)) {
                                                    stuIter.remove();
                                                } else if (!((List) listGenericTypeObj).contains(aObj)) {
                                                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed, Enum Constants: " + listGenericTypeObj.toString());
                                                } else {
                                                    enumList.add(aObj);
                                                }
                                            } else if (aObj != null) {
                                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed, Enum Constants: " + listGenericTypeObj.toString());
                                            }//value == null ,make it pass
                                        }

                                    } else {
                                        throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : Type Define in DB is wrong.");
                                    }
                                }

                            }
                        } else {
                            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to [] failed");
                        }

                    } else if ("java.util.List".equals(valueType)) {
                        if (!(value instanceof List)) {
                            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to [] failed");
                        }
                    } else {
                        // valueType is self define class name or Enum class name , in typeMap , Enum class is []
                        Object classTypeObj = typeMap.get(valueType);

                        if (classTypeObj instanceof List) {//枚举类
                            if (value instanceof Number) {
                                int _index = TypeUtils.castToInt(value);
                                if (((List) classTypeObj).get(_index) != null) {
                                    String enumName = ((List) classTypeObj).get(_index).toString();
                                    mapEntry.setValue(enumName);
                                } else {
                                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed, Enum Constants: " + classTypeObj.toString());
                                }

                            } else if (value instanceof String) {
                                if ("".equals(value)) {
                                    mapEntry.setValue(null);
                                } else if (!((List) classTypeObj).contains(value)) {
                                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed, Enum Constants: " + classTypeObj.toString());
                                }
                            } else if (value != null) {
                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to Enum failed");
                            }//value == null ,make it pass


                        } else if (classTypeObj instanceof Map) { //普通自定义类
                            if (!(value instanceof Map)) {
                                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : JSON input " + key + "=" + value + " parse to {} failed");
                            }
                            checkReviseRetMap((Map) value, (Map) classTypeObj);   //递归check........，value 要验证是个Map，

                        } else {
                            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : Type Define in DB is wrong.");
                        }


                    }

                }

            }
        } catch (ReturnCodeException e) {
            throw e;
        } catch (JSONException e) {
            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : " + e.getMessage() + ", JSON Param parsed Map is " + retMap, e);
        } catch (Exception e) {
            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : " + e.getMessage() + ", JSON Param parsed Map is " + retMap, e);
        }

    }

    public static String checkReviseObjArrayParam(String paramName, String oriStr, String paramType, String paramTypeJson) {
        if (oriStr == null) {
            return null;
        }
        Map retMap = new HashMap();
        JSONArray jsonArray = null;
        try {
            jsonArray = JSON.parseArray(oriStr);
        } catch (Exception e) {
            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : param value is " + oriStr + ", but param type is " + paramType);
        }

        retMap.put(paramName, jsonArray);
        Map typeMap = JSON.parseObject(paramTypeJson, Map.class);
        typeMap.put(paramName, paramType);
        checkReviseRetMap(retMap, typeMap);
        return JSON.toJSONString(retMap.get(paramName));
    }

    public static String checkReviseObjParam(String paramName, String oriStr, String paramType, String paramTypeJson) {
        Map retMap = new HashMap();
        if (paramType.endsWith(Constants.LIST_TYPE_SUFFIX)) {
            JSONArray jsonArray = null;
            try {
                jsonArray = JSON.parseArray(oriStr);
            } catch (Exception e) {
                throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : param value is " + oriStr + ", but param type is " + paramType);
            }
            retMap.put(paramName, jsonArray);

            Map typeMap = JSON.parseObject(paramTypeJson, Map.class);
            typeMap.put(paramName, paramType);
            checkReviseRetMap(retMap, typeMap);
            return JSON.toJSONString(retMap.get(paramName));
        } else {
            Map typeMap = JSON.parseObject(paramTypeJson, Map.class);
            typeMap.put(paramName, paramType);
            if (typeMap.get(paramType) instanceof List) {//枚举类
                try {
                    if ("".equals(oriStr))
                        oriStr = " ";
                    Integer enumIntValue = TypeUtils.castToInt(oriStr);
                    retMap.put(paramName, enumIntValue);
                } catch (Exception e) {
                    retMap.put(paramName, oriStr);
                }
            } else {
                Map jsonMap = null;
                try {
                    jsonMap = JSON.parseObject(oriStr, Map.class);
                } catch (Exception e) {
                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : param value is " + oriStr + ", but param type is " + paramType);
                }
                retMap.put(paramName, jsonMap);
            }
            checkReviseRetMap(retMap, typeMap);
            return JSON.toJSONString(retMap.get(paramName));
        }
    }

    public static Object convertParamType(String paramName, String param, String paramType, String paramTypeJson) {
        if (param == null) {
            return null;
        } else if ("java.lang.String".equals(paramType)) {
            return param;
        } else if ("boolean".equals(paramType)) {
            return Boolean.valueOf(Boolean.parseBoolean(param));
        } else if ("long".equals(paramType)) {
            return Long.valueOf(Long.parseLong(param));
        } else if ("int".equals(paramType)) {
            return Integer.valueOf(Integer.parseInt(param));
        } else if ("float".equals(paramType)) {
            return Float.valueOf(Float.parseFloat(param));
        } else if ("double".equals(paramType)) {
            return Double.valueOf(Double.parseDouble(param));
        } else if ("short".equals(paramType)) {
            return Short.valueOf(Short.parseShort(param));
        } else if ("byte".equals(paramType)) {
            return Byte.valueOf(Byte.parseByte(param));
        } else if ("java.lang.Boolean".equals(paramType)) {
            return Boolean.valueOf(param);
        } else if ("java.lang.Integer".equals(paramType)) {
            return Integer.valueOf(param);
        } else if ("java.lang.Long".equals(paramType)) {
            return Long.valueOf(param);
        } else if ("java.lang.Float".equals(paramType)) {
            return Float.valueOf(param);
        } else if ("java.lang.Short".equals(paramType)) {
            return Short.valueOf(param);
        } else if ("java.lang.Double".equals(paramType)) {
            return Double.valueOf(param);
        } else if ("java.util.Date".equals(paramType)) {
            try {
                return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).parse(param);
            } catch (ParseException var6) {
                try {
                    long e2 = Long.parseLong(param);
                    return new Date(e2);
                } catch (Throwable var5) {
                    throw new IllegalArgumentException(var6);
                }
            }
        } else if ("java.sql.Timestamp".equals(paramType)) {
            try {
                long complexDecoder1 = Long.parseLong(param);
                return new Timestamp(complexDecoder1);
            } catch (Throwable var7) {
                throw new IllegalArgumentException(var7);
            }
        } else {
            if (Constants.LIST_STRING_TYPE.equals(paramType) || Constants.LIST_TYPE_NAME.equals(paramType)) {
                param = checkReviseListStringParam(param, paramType);
            } else if (paramTypeJson != null) {
                param = checkReviseObjParam(paramName, param, paramType, paramTypeJson);
            }

            ComplexDecoder complexDecoder = new ComplexDecoder(param);
            complexDecoder.decode();
            return complexDecoder.getNativeObj4Json();
        }
    }

    public static String checkReviseListStringParam(String oriStr, String paramType) {
        JSONArray jsonArrayTemp = null;
        try {
            jsonArrayTemp = JSON.parseArray(oriStr);
        } catch (Exception e) {
            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : param value is " + oriStr + ", but param type is " + paramType);
        }
        JSONArray jsonArray = new JSONArray();
        for (Object obj : jsonArrayTemp) {
            if (obj != null) {
                jsonArray.add(obj);
            }
        }
        if ("java.util.List".equals(paramType)) {
            return JSON.toJSONString(jsonArray);
        } else {
            List listValue = (List) jsonArray;
            for (int i = 0; i < listValue.size(); i++) {
                String stringValue = TypeUtils.castToString(listValue.get(i));
                listValue.set(i, stringValue);  // String 应该是可以传入null的，所以这里直接赋值
            }
            return JSON.toJSONString(listValue);
        }
    }

    public static void removeClassName(Object pojo) {
        if (pojo != null) {
            if (pojo.getClass().isArray()) {
                int len = Array.getLength(pojo);
                for (int i = 0; i < len; i++) {
                    Object obj = Array.get(pojo, i);
                    removeClassName(obj);
                }
            }
            if (pojo instanceof Collection<?>) {
                Collection<Object> src = (Collection<Object>) pojo;
                for (Object obj : src) {
                    removeClassName(obj);
                }
            }
            if (pojo instanceof Map<?, ?>) {
                Map<Object, Object> src = (Map<Object, Object>) pojo;

                Iterator<Map.Entry<Object, Object>> it = src.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Object, Object> obj = it.next();
                    if ("class".equals(obj.getKey())) {
                        it.remove();
                    } else {
                        removeClassName(obj.getValue());
                    }
                }
            }
        }
    }
}
