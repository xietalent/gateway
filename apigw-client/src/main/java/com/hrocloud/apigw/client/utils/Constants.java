package com.hrocloud.apigw.client.utils;

import com.hrocloud.apigw.client.define.ConstField;


public class Constants {
    public static final  String              ACCESS_SPLITTER           = new String(new char[]{' ', 1});
    public static final   String          SERVER_ADDRESS           = "a:";
    public static final   String          SPLIT                    = "|";
    public static final   String          REQ_TAG                  = "s:";
    public static final   String          THREAD_ID                 = "t:";
    public static final   String          CLIENT_IP                = "_clientIP";
    public static final    String          HTTPMETHOD_POST          = "POST";
    public static final    String          FORMAT_XML               = "xml";
    public static final    String          FORMAT_JSON              = "json";
    public static final    String          FORMAT_PROTOBUF          = "protobuf";
    public static final   String          SMS_PASSWORD_LOGIN       = "user.smsPasswordLogin";
    public static final    String          DEBUG_AGENT              = "pajk.tester";
    public static final   String          CONTENT_TYPE_XML         = "application/xml; charset=utf-8";
    public static final   String          CONTENT_TYPE_JSON        = "application/json; charset=utf-8";
    public static final   String          CONTENT_TYPE_OCSTREAM    = "application/octet-stream";
    public static final   String          HEADER_ORGIN             = "Access-Control-Allow-Origin";
    public static final   String          HEADER_METHOD            = "Access-Control-Allow-Method";
    public static final   String          HEADER_CREDENTIALS       = "Access-Control-Allow-Credentials";
    public static final   String          HEADER_METHOD_VALUE      = "POST, GET, OPTIONS, PUT, DELETE, HEAD";
    public static final   String          HEADER_CREDENTIALS_VALUE = "true";
    public static final   String          JSONARRAY_PREFIX         = "[";
    public static final   String          JSONARRAY_SURFIX         = "]";
    public static final   String          JSONOBJECT_PREFIX         = "{";
    public static final   String          JSONOBJECT_SURFIX         = "}";
    public static final   String          JSONOBJECT_VALUE_PREFIX         = "{\"value\":";
    public static final byte[]          JSON_SPLIT               = ",".getBytes(ConstField.UTF8);
    public static final String          JSON_END                 = "]}";
    public static final byte[]          JSON_EMPTY               = "{}".getBytes(ConstField.UTF8);
    public static final byte[]          XML_EMPTY                = "<empty/>".getBytes(ConstField.UTF8);
    public static final String            RESULT_KEY               = "value";
    public static final String          JSON_STAT                = "{\"stat\":";
    public static final String         JSON_CONTENT             = ",\"content\":[";
    public static final String        DEVICE_REGISTER           = "device.register";
    public static final String        DEVICE_REGISTER_FINGER    = "device.registerFinger";
    public static final String        DEVICE_REGISTER_FINGER_KEY    = "device.registerFingerKey";
    public static final String        DEVICE_CHECK_SECURITY     = "device.checkSecurity";
    public static final String       ARRAY_TYPE_SUFFIX         = "[]";
    public static final String        LIST_TYPE_SUFFIX         = ">";
    public static final byte[]        LIST_STRING_EMPTY_RESULT = "{\"value\":[]}".getBytes(ConstField.UTF8);
    public static final String        LIST_STRING_TYPE         = "java.util.List<java.lang.String>";
    public static final String        LIST_TYPE_NAME         = "java.util.List";
    public static final String        JSON_STRING_TYPE_NAME         = "net.pocrd.responseEntity.JSONString";
    public static final String        RAW_STRING_TYPE_NAME         = "net.pocrd.responseEntity.RawString";
    public static final String        CLASS_NAME_STR         = "class";
    public static final String        CONTENT_TYPE         = "Content-Type";
    public static final String        CONTENT_TYPE_CHARSET_UPPER         = "charset=UTF-8";
    public static final String        CONTENT_TYPE_CHARSET_LOWER         = "charset=UTF-8";
    public static final String        CONTENT_TYPE_CHARSET_ISO = "ISO-8859-1";
    public static final String        CODE_SUCCESS_NAME = "SUCCESS";

}
