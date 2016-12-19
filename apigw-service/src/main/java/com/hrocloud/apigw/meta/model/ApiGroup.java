package com.hrocloud.apigw.meta.model;

import java.io.Serializable;

public class ApiGroup implements Serializable {

    private String groupId;

    private String groupName;

    private long beginCode;

    private long endCode;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getBeginCode() {
        return beginCode;
    }

    public void setBeginCode(long beginCode) {
        this.beginCode = beginCode;
    }

    public long getEndCode() {
        return endCode;
    }

    public void setEndCode(long endCode) {
        this.endCode = endCode;
    }
}
