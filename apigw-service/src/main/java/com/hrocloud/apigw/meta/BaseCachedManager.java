package com.hrocloud.apigw.meta;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by hanzhihua on 2016/11/21.
 */
public abstract class BaseCachedManager<T extends Serializable> {

    protected static final Logger log = LoggerFactory.getLogger(BaseCachedManager.class);


    public static CacheManager cacheManager = CacheManager.create(BaseCachedManager.class.getResource("/ehcache.xml"));


    protected abstract String getNameSpace();

    protected <T> T getCache(Object key) {
        log.debug("getString:key=[{}]", key);
        Cache cache = cacheManager.getCache(getNameSpace());
        Element element = cache.get(key);
        T value;
        if (element == null) {
            log.warn("getCache:key=[{}],value=null", key);
            return null;
        } else {
            value = (T) element.getObjectValue();
            log.debug("getCache:key=[{}], value=[{}]", key, value);
            return value;
        }
    }

    protected void deleteCache(Object key) {
        log.debug("deleteCache:key=[{}]", key);
        Cache cache = cacheManager.getCache(getNameSpace());
        cache.remove(key);
    }

    protected void putCache(Object key, T value) {
        log.debug("putCache:key=[{}], value=[{}]", key, value);
        Cache cache = cacheManager.getCache(getNameSpace());
        Element element = cache.get(key);
        if (element == null) {
            element = new Element(key, value);
        }
        cache.put(element);
        log.debug("putCache:key=[{}],value=[{}]", key, value);
    }
}
