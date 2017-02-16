package com.hrocloud.apigw.service;

import com.hrocloud.apigw.client.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SecurityBiz {

	private static final Logger logger = LoggerFactory.getLogger(SecurityBiz.class);

	@Resource
	private SecurityService securityService;

	public int checkUserStatus(String clientIp, int appId,
			long deviceId, int userId) {

		int i = securityService.checkUserStatus(clientIp,
				deviceId, appId, userId);
		logger.info(
				"checkUserStatus result:{},clientIp:{},appId:{},deviceId:{},userId:{}",
				i, clientIp, appId, deviceId, userId);

		return i;
	}
}
