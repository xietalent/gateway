package com.hrocloud.apigw.client.service;

public interface SecurityService {

    int checkUserStatus(String clientIp, Long deviceId, Long applicationId, Long userId);
}