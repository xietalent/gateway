package com.hrocloud.apigw.utils;

import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.hrocloud.apigw.meta.model.ApiInterface;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DubboUtil {

    private static Map<String, GenericService> services = new ConcurrentHashMap<>();

    private static Map<String, ApiInterface> invokeInterfaces = new ConcurrentHashMap<>();


    private static ApplicationConfig application;
    private static RegistryConfig registry;
    private static MonitorConfig monitorConfig;
    private static ConsumerConfig consumer;

    public DubboUtil(String appName, String registryAddress) {
        application = new ApplicationConfig();
        application.setName(appName);

        registry = new RegistryConfig();
        registry.setProtocol("zookeeper");
        registry.setAddress(registryAddress);
//        registry.set;

        monitorConfig = new MonitorConfig();
        monitorConfig.setProtocol("registry");

        consumer = new ConsumerConfig();
//        consumer.setTimeout(timeout);
        consumer.setRetries(0);
    }


    public static GenericService getService(ApiInterface apiInterface, String version) {
        System.out.println(apiInterface.getInvokeInterface());
        long start = System.currentTimeMillis();
        System.out.println("start:"+start);
        System.out.println("start:"+start + "  apiInterface" + apiInterface);

        GenericService service = services.get(apiInterface.getInvokeInterface());
        if (service != null) {
            return service;
        }

        synchronized (apiInterface) {
            service = services.get(apiInterface.getInvokeInterface());
            if (service != null) {
                return service;
            }

            ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<GenericService>();
            referenceConfig.setApplication(application);
            referenceConfig.setMonitor(monitorConfig);
            referenceConfig.setRegistry(registry);
            referenceConfig.setConsumer(consumer);
            referenceConfig.setInterface(apiInterface.getInvokeInterface());
            referenceConfig.setVersion(version);
            referenceConfig.setGeneric(true);
//            referenceConfig.setUrl("dubbo://127.0.0.1:20880");
            service = referenceConfig.get();
            if (service != null) {
                services.put(apiInterface.getInvokeInterface(), service);
            }
            System.out.println("start:"+start+ "  end:"+ System.currentTimeMillis());
        }

        return service;
    }

    public static ApiInterface getApiInterface(String apiInterface) {
        ApiInterface mobileApiInterface = invokeInterfaces.get(apiInterface);
        if (mobileApiInterface != null) {
            return mobileApiInterface;
        }

        //synchronized and double check
        synchronized (DubboUtil.class) {
            mobileApiInterface = invokeInterfaces.get(apiInterface);
            if (mobileApiInterface != null) {
                return mobileApiInterface;
            }
            mobileApiInterface = new ApiInterface();
            mobileApiInterface.setInvokeInterface(apiInterface);
            invokeInterfaces.put(apiInterface, mobileApiInterface);
            return mobileApiInterface;
        }
    }

}
