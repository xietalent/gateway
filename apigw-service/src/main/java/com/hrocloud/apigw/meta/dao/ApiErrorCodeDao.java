package com.hrocloud.apigw.meta.dao;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface ApiErrorCodeDao {

    String getErrorCodeName(Map<String, String> map);

}
