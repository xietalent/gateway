package com.hrocloud.apigw.meta.dao;


import com.hrocloud.apigw.meta.model.ApiMetaData;

import java.util.Map;


public interface ApiMetaDataDao {

    ApiMetaData getMobileApiByIdVersion(Map<String, String> map);

    ApiMetaData getMobileApiByGroupNameVersion(Map<String, String> map);


}
