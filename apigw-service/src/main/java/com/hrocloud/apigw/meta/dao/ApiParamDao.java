package com.hrocloud.apigw.meta.dao;



import com.hrocloud.apigw.meta.model.ApiParam;

import java.util.List;

public interface ApiParamDao {

    List<ApiParam> getByMobileApiId(String apiId);

}
