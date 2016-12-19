package com.hrocloud.apigw.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hrocloud.apigw.client.define.*;
import com.hrocloud.apigw.client.utils.*;
import com.hrocloud.apigw.meta.ApiErrorCodeManager;
import com.hrocloud.apigw.meta.ApiMetaDataManager;
import com.hrocloud.apigw.meta.ApiParamManager;
import com.hrocloud.apigw.utils.*;
import com.hrocloud.apigw.utils.AccessLogger;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

@Service
public class ApiGatewayService {

    protected static final ApiMethodCall[] EMPTY_METHOD_CALL_ARRAY  = new ApiMethodCall[0];

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayService.class);

    @Resource
    private AccessLogger access;

    @Resource
    private ApiMetaDataManager apiMetaDataManager;

    @Resource
    private ApiParamManager apiParamManager;

    @Resource
    private ApiErrorCodeManager apiErrorCodeManager;

    @Resource
    private ApiConfig apiConfig;

    @Resource
    private SecurityBiz securityBiz;

    @Value("${dubbo.version}")
    private String dubboVersion;

    public void processRequest(HttpServletRequest request, HttpServletResponse response) {

        AbstractReturnCode parseResult = null;

        ApiContext apiContext = ApiContext.getCurrent();

        long current = System.currentTimeMillis();
        try {
            apiContext.clear();
            apiContext.startTime = current;

            RequestProcessor processRequestProcessor = new RequestProcessor(this.apiConfig);

            processRequestProcessor.process(request,response,apiContext,access);

            MethodProcessor methodProcessor = new MethodProcessor(apiMetaDataManager,apiParamManager,apiConfig,access,securityBiz,dubboVersion);

            parseResult = methodProcessor.process(apiContext, request);

            if (parseResult == ApiReturnCode.SUCCESS && apiContext.caller != null && apiContext.caller.expire < current && SecurityType.UserLogin.check(
                    apiContext.requiredSecurity)) {
                parseResult = ApiReturnCode.TOKEN_EXPIRE;
            }
        } catch (Exception e) {
            logger.error("init request failed.", e);
            apiContext.fatalError = true;
        }

        try {
            if (apiContext.fatalError) {
                access.logRequest("with fatal error", "FATAL_ERROR");
            } else if (parseResult != ApiReturnCode.SUCCESS) {
                access.logRequest("with error", String.valueOf(parseResult.getCode()));
            } else {
                access.logRequest();

                InvokeProcessor invokeProcessor = new InvokeProcessor( request, response, access);
                invokeProcessor.process(apiContext);
            }
        } catch (SerializeException se) {
            logger.error("output failed.", se.getException());
            apiContext.fatalError = true;
        } catch (Throwable t) {
            logger.error("api execute error.", t);
            apiContext.fatalError = true;
        } finally {
            try {
                if (apiContext.fatalError) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
                } else if (parseResult != ApiReturnCode.SUCCESS) {
                    Exception e = output(apiContext, parseResult, EMPTY_METHOD_CALL_ARRAY, request, response);
                    if (e != null) {
                        logger.error("output failed.", e);
                    }
                } else {
                    Exception e = output(apiContext, ApiReturnCode.SUCCESS,
                            apiContext.apiCallInfos.toArray(new ApiMethodCall[apiContext.apiCallInfos.size()]), request, response);
                    if (e != null) {
                        logger.error("output failed.", e);
                    }
                }
            } catch (Exception e) {
                logger.error("output failed.", e);
            }
            if (apiContext != null) {
                apiContext.clear();
            }
        }
    }

    private Exception output(ApiContext apiContext, AbstractReturnCode code, ApiMethodCall[] calls, HttpServletRequest request,
                             HttpServletResponse response) {
        Exception outputException = null;

        try {
            Response apiResponse = new Response();
            apiResponse.systime = System.currentTimeMillis();
            apiResponse.code = code.getDisplay().getCode();
            if (apiContext.localException != null) {
                apiResponse.data = apiContext.localException.getData();
            }
            apiResponse.stateList = new ArrayList<CallState>(calls.length);
            if (apiContext.cid != null) {
                apiResponse.cid = apiContext.cid;
            }
            for (ApiMethodCall call : calls) {
                CallState state = new CallState();
                state.code = call.getReturnCode().getCode();
                if (call.getReturnCode().getName() != null) {
                    state.msg = call.getReturnCode().getName();
                }else {
                    state.msg = apiErrorCodeManager.getErrorCodeName(state.code, this.dubboVersion,call.apiMetaData.getGroupName());
                }
                state.length = call.resultLen;
                apiResponse.stateList.add(state);
            }
            apiResponse.notificationList = apiContext.getNotifications();

            PrintWriter printWriter = response.getWriter();
            response.setStatus(HttpServletResponse.SC_OK);

            StringBuilder respStrBuilder = new StringBuilder("");
            respStrBuilder.append(Constants.JSON_STAT);
            Object ret = DubboBeanCodecUtil.generalize(apiResponse);
            MiscUtil.removeClassName(ret);
            String respObjJson = JSON.toJSONString(ret, new SerializerFeature[]{SerializerFeature.WriteMapNullValue});
            respStrBuilder.append(respObjJson);
            respStrBuilder.append(Constants.JSON_CONTENT);
            respStrBuilder.append(apiContext.outputStream.toString(ConstField.UTF8.name()));
            respStrBuilder.append(Constants.JSON_END);
            printWriter.print(respStrBuilder.toString());
            printWriter.flush();

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            outputException = e;
        }
        return outputException;
    }

}
