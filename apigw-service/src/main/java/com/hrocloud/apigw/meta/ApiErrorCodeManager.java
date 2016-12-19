package com.hrocloud.apigw.meta;


import com.hrocloud.apigw.meta.dao.ApiErrorCodeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiErrorCodeManager extends BaseCachedManager<String> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ApiErrorCodeDao mobileApiErrorCodeDao;

    public String getErrorCodeName(int errorCode, String version, String group) {
        String key = group + "-" + String.valueOf(errorCode) + "-" + version;
        String codeName = getCache(key);
        if (codeName != null) {
            return codeName;
        }
        try {
            Map<String, String> map = new HashMap<>();
            map.put("errorCode", String.valueOf(errorCode));
            map.put("version", version);

            codeName = mobileApiErrorCodeDao.getErrorCodeName(map);
            if (codeName != null) {
                putCache(key,codeName);
            } else {
                logger.info("getErrorCodeName error No error code for {} in db.", String.valueOf(errorCode));
            }
        } catch (Exception e) {
            logger.info("getErrorCodeName error there may be multi code data in db for code:" + errorCode);
        }
        return codeName;
    }


    protected String getNameSpace() {
        return "apiErrorCode";
    }

}
