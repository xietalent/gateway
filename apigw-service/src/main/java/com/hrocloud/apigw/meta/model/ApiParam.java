package com.hrocloud.apigw.meta.model;

import java.io.Serializable;

public class ApiParam implements Serializable {

    private String apiId;

    private int paramOrder;

    private String paramName;

    private String paramType;

    private boolean isAuto;

    private boolean isRsaEncrypted;

    private boolean isAesEncrypted;

    private boolean isRequired;

    private String defaultValue;

    private String paramTypeDetail;

    public String getParamTypeDetail() {
        return paramTypeDetail;
    }

    public void setParamTypeDetail(String paramTypeDetail) {
        this.paramTypeDetail = paramTypeDetail;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public int getParamOrder() {
        return paramOrder;
    }

    public void setParamOrder(int paramOrder) {
        this.paramOrder = paramOrder;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setIsAuto(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public boolean isRsaEncrypted() {
        return isRsaEncrypted;
    }

    public void setIsRsaEncrypted(boolean isRsaEncrypted) {
        this.isRsaEncrypted = isRsaEncrypted;
    }

    public boolean isAesEncrypted() {
        return isAesEncrypted;
    }

    public void setIsAesEncrypted(boolean isAesEncrypted) {
        this.isAesEncrypted = isAesEncrypted;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }
}
