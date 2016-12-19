package com.hrocloud.apigw.meta;

import com.hrocloud.apigw.meta.dao.ApiGroupDao;
import com.hrocloud.apigw.meta.model.ApiGroup;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by hanzhihua on 2016/11/21.
 */
@Service
public class ApiGroupManager extends BaseCachedManager<ApiGroup> {

    @Resource
    private ApiGroupDao mobileApiGroupDao;

    public ApiGroup getMobileGroupById(String groupId) {
        ApiGroup group = getCache(groupId);
        if (group != null) {
            return group;
        }
        group = mobileApiGroupDao.getMobileGroupById(groupId);
        if (group != null) {
            putCache(groupId,group);
        }
        return group;
    }


    protected String getNameSpace() {
        return "apiGroup";
    }

}
