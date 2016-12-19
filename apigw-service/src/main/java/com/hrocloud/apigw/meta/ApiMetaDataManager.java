package com.hrocloud.apigw.meta;

import com.hrocloud.apigw.meta.dao.ApiMetaDataDao;
import com.hrocloud.apigw.meta.model.ApiMetaData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hanzhihua on 2016/11/21.
 */
@Service
public class ApiMetaDataManager extends BaseCachedManager<ApiMetaData>{

    @Resource
    private ApiMetaDataDao mobileApiMetaDataDao;

    public ApiMetaData getMobileApiByIdVersion(String apiId, String version) {
        Map<String, String> map = new HashMap<>();
        map.put("apiId", apiId);
        map.put("version", version);
        String key = apiId + "-" + version;
        ApiMetaData apiMetaData = getCache(key);

        if (apiMetaData == null) {
            apiMetaData = mobileApiMetaDataDao.getMobileApiByIdVersion(map);
            putCache(key, apiMetaData);
        }
        return apiMetaData;
    }

    public ApiMetaData getMobileApiByGroupNameVersion(String group, String name, String version) {
        Map<String, String> map = new HashMap<>();
        map.put("apiGroup", group);
        map.put("apiName", name);
        map.put("version", version);
        String key = group + "-" + name + "-" + version;
        ApiMetaData apiMetaData = getCache(key);

        if (apiMetaData == null) {
            synchronized (this) {
                apiMetaData = getCache(key);
                if (apiMetaData == null) {
                    apiMetaData = mobileApiMetaDataDao.getMobileApiByGroupNameVersion(map);
                    putCache(key, apiMetaData);
                }
            }

        }
        return apiMetaData;
    }

    protected String getNameSpace() {
        return "apiMeta";
    }
}
