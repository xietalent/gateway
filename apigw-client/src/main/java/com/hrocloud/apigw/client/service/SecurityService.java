package com.hrocloud.apigw.client.service;

public interface SecurityService {

    int checkUserStatus(String clientIp, Long deviceId, Integer applicationId, Integer userId);
}