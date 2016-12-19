package com.hrocloud.apigw.service;

import com.hrocloud.apigw.client.define.CallerInfo;
import com.hrocloud.apigw.client.define.CommonParameter;
import com.hrocloud.apigw.client.define.ConstField;
import com.hrocloud.apigw.client.utils.AESTokenHelper;
import com.hrocloud.apigw.client.utils.AesHelper;
import com.hrocloud.apigw.client.utils.Base64Util;
import com.hrocloud.apigw.client.utils.MiscUtil;
import com.hrocloud.apigw.utils.AccessLogger;
import com.hrocloud.apigw.utils.ApiConfig;
import com.hrocloud.apigw.utils.ApiContext;
import com.hrocloud.apigw.client.utils.Constants;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Enumeration;

/**
 * Created by hanzhihua on 2016/11/22.
 */
class RequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    private final ApiConfig apiConfig;

    RequestProcessor(ApiConfig apiConfig){
        this.apiConfig = apiConfig;
    }

    public void process(final HttpServletRequest request, final HttpServletResponse response,final ApiContext context,final AccessLogger access){

        context.startTime = System.currentTimeMillis();

        context.agent = request.getHeader(HttpHeaders.USER_AGENT);

        context.clientIP = MiscUtil.getClientIP(request);

        context.cid = request.getHeader(CommonParameter.callId);

        if (context.cid != null && context.cid.length() > 16) {
            context.cid = null;
        }

        if (context.cid == null) {
            context.cid = Constants.REQ_TAG + context.startTime;
        }

        try {
            context.cid = Constants.SERVER_ADDRESS + access.getServerAddress()
                    + Constants.SPLIT + Constants.THREAD_ID + Thread.currentThread().getId() + Constants.SPLIT + context.cid;
        } catch (Exception e) {
            logger.error("can not get server address,_cid may be not unique", e);
        }

        context.deviceId = request.getParameter(CommonParameter.deviceId);

        context.uid = request.getParameter(CommonParameter.userId);

        MDC.clear();

        MDC.put(CommonParameter.callId, context.cid);

        MDC.put(Constants.CLIENT_IP, context.clientIP);

        String appid = request.getParameter(CommonParameter.applicationId);

        context.appid = (appid != null && appid.length() != 0) ? Integer.parseInt(appid) : 0;

        String httpMethod = request.getMethod();

        if (Constants.HTTPMETHOD_POST.equalsIgnoreCase(httpMethod)) {
            StringBuilder sb = new StringBuilder(256);
            Enumeration<String> keys = request.getParameterNames();

            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                if (key != null) {
                    sb.append(key);
                    sb.append("=");
                    sb.append(request.getParameter(key));
                    sb.append("&");
                }
            }
            context.requestInfo = sb.toString();
        } else {
            context.requestInfo = request.getQueryString();
        }

        try {
            context.token = request.getParameter(CommonParameter.token);
            Cookie[] cs = request.getCookies();
            if (context.token == null || context.token.length() == 0) {
                if (cs != null) {
                    for (Cookie c : cs) {
                        if (CommonParameter.token.equals(c.getName())) {
                            if (c.getValue() != null && !c.getValue().isEmpty()) {
                                String tk = URLDecoder.decode(c.getValue(), "utf-8");
                                tk = tk.charAt(0) == '\"' ? tk.substring(1, tk.length()) : tk;
                                tk = tk.charAt(tk.length() - 1) == '\"' ? tk.substring(0, tk.length() - 1) : tk;
                                context.token = tk;
                            }
                        }

                    }
                }
            }

            if (context.token != null && context.token.length() > 0) {
                context.caller = parseCallerFromUserToken(Base64Util.decode(context.token));
            }
        } catch (Exception e) {
            logger.error("parse token failed.", e);
        }

        response.setCharacterEncoding(ConstField.UTF8.name());
        response.setContentType(Constants.CONTENT_TYPE_JSON);
        setCORSHeader(request, response);
    }

    private CallerInfo parseCallerFromUserToken(byte[] token) {
        CallerInfo caller = null;
        if (token != null && token.length > 0) {
            AESTokenHelper aesTokenHelper = new AESTokenHelper(new AesHelper(Base64Util.decode(apiConfig.getApiTokenAes()), null));
            caller = aesTokenHelper.parseUserToken(token);
            if (caller != null) {
                MDC.put(CommonParameter.applicationId, String.valueOf(caller.appid));
                MDC.put(CommonParameter.deviceId, String.valueOf(caller.deviceId));
                MDC.put(CommonParameter.userId, String.valueOf(caller.uid));
            }
        }
        return caller;
    }

    private void setCORSHeader(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        origin = origin == null ? request.getHeader("Referer") : origin;
        if (origin != null) {
            response.setHeader(Constants.HEADER_ORGIN, origin);
            response.addHeader(Constants.HEADER_METHOD, Constants.HEADER_METHOD_VALUE);
            response.setHeader(Constants.HEADER_CREDENTIALS, Constants.HEADER_CREDENTIALS_VALUE);
        }
    }
}
