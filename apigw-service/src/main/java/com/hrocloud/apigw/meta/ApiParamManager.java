package com.hrocloud.apigw.meta;

import com.hrocloud.apigw.meta.dao.ApiParamDao;
import com.hrocloud.apigw.meta.model.ApiParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanzhihua on 2016/11/21.
 */
@Service
public class ApiParamManager extends BaseCachedManager<ArrayList<ApiParam>> {

    @Resource
    private ApiParamDao mobileApiParamDao;

    public List<ApiParam> listInputParamByApiId(String apiId, String group) {
        String key = group + "-" + apiId;
        List<ApiParam> params = getCache(key);

        if (params == null) {
            synchronized (this) {
                params = getCache(key);
                if (params == null) {
                    params = mobileApiParamDao.getByMobileApiId(apiId);
                    if (params != null) {
                        putCache(key, (ArrayList) params);
                    }
                }
            }
        }
        return params;
    }


    protected String getNameSpace() {
        return "apiParam";
    }

}
