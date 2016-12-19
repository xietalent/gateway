package com.hrocloud.apigw.meta.model;

import java.io.Serializable;
import java.util.Arrays;

public class ApiMetaData implements Serializable {


    /**
     * 服务id
     */
    private String apiId;

    private String invokeInterface;

    private String invokeMethod;

    private String version;

    private String status;

    private boolean isPublic;

    private int securityLevel;

    private String apiGroup;

    private String apiName;

    private String groupName;

    private String designedErrorCode;

    private String methodName;

    private int[] errors;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getInvokeInterface() {
        return invokeInterface;
    }

    public void setInvokeInterface(String invokeInterface) {
        this.invokeInterface = invokeInterface;
    }

    public String getInvokeMethod() {
        return invokeMethod;
    }

    public void setInvokeMethod(String invokeMethod) {
        this.invokeMethod = invokeMethod;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getGroupName() { return groupName; }

    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getMethodName() {
        if (methodName==null) {
            methodName = this.groupName +"."+this.apiName;
        }
        return this.methodName;
    }

    public String getDesignedErrorCode() {
        return designedErrorCode;
    }

    public int[] getErrors() {
        if (errors==null) {
            if (designedErrorCode!=null) {
                String[] codes = designedErrorCode.split(",");
                errors = new int[codes.length];
                for (int i=0;i<codes.length;i++) {
                    errors[i] = Integer.parseInt(codes[i].trim());
                }
                Arrays.sort(errors);
            }else {
                errors = new int[0];
            }
        }
        return errors;
    }


}
