package com.hrocloud.apigw.utils;

import com.hrocloud.apigw.meta.model.ApiMetaData;
import com.hrocloud.apigw.client.define.AbstractReturnCode;
import com.hrocloud.apigw.client.define.ApiReturnCode;

public class ApiMethodCall {
    private static ApiMethodCall UnknownMethodCall;

    static {
        UnknownMethodCall = new ApiMethodCall();
        UnknownMethodCall.apiMetaData = new ApiMetaData();
        UnknownMethodCall.originCode = ApiReturnCode.UNKNOWN_METHOD;
        UnknownMethodCall.returnCode = ApiReturnCode.UNKNOWN_METHOD;
    }

    private ApiMethodCall() {}

    public ApiMethodCall(ApiMetaData apiMetaData) {
        returnCode = ApiReturnCode.NO_ASSIGN;
        originCode = ApiReturnCode.NO_ASSIGN;
        this.apiMetaData = apiMetaData;
    }

    public ApiMetaData apiMetaData;

    public Object  result;

    public int resultLen;

    public StringBuilder message = new StringBuilder();

    public long startTime;

    public int costTime;

    private AbstractReturnCode returnCode = ApiReturnCode.SUCCESS;

    private AbstractReturnCode originCode = ApiReturnCode.SUCCESS;

    public String[] parameterTypes;

    public String[] parameters;

    public String[] parameterNames;

    public String returnTypeName;

    public String[] parameterTypeJsons;

    public void setReturnCode(AbstractReturnCode code) {
        if (returnCode == ApiReturnCode.NO_ASSIGN) {
            returnCode = code.getDisplay();
            originCode = code;
        }
    }

    public AbstractReturnCode getReturnCode() {
        return returnCode;
    }

    public void replaceReturnCode(AbstractReturnCode code) {
        returnCode = code;
    }

    public AbstractReturnCode getOriginCode() {
        return originCode;
    }
}