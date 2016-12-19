package com.hrocloud.apigw.service;

import com.hrocloud.apigw.client.define.*;
import com.hrocloud.apigw.client.dubboext.DubboExtProperty;
import com.hrocloud.apigw.client.utils.*;
import com.hrocloud.apigw.meta.ApiMetaDataManager;
import com.hrocloud.apigw.meta.ApiParamManager;
import com.hrocloud.apigw.meta.model.ApiMetaData;
import com.hrocloud.apigw.meta.model.ApiParam;
import com.hrocloud.apigw.utils.AccessLogger;
import com.hrocloud.apigw.utils.ApiConfig;
import com.hrocloud.apigw.utils.ApiContext;
import com.hrocloud.apigw.utils.ApiMethodCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by hanzhihua on 2016/11/22.
 */
public class MethodProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MethodProcessor.class);

    private final ApiMetaDataManager apiMetaDataManager;

    private final ApiParamManager apiParamManager;

    private String dubboVersion;

    private final ApiConfig apiConfig;

    private final AccessLogger access;

    private final SecurityBiz securityBiz;

    private static int SECURITY_WITH_USER_TK = 0;

    static  {
        SecurityType[] securityTypes = SecurityType.values();
        if (securityTypes != null) {
            for (SecurityType securityType : securityTypes) {
                if (securityType.isNeedUserToken()) {
                    SECURITY_WITH_USER_TK = securityType.authorize(SECURITY_WITH_USER_TK);
                }
            }
        }
    }

    MethodProcessor(ApiMetaDataManager apiMetaDataManager, ApiParamManager apiParamManager, ApiConfig apiConfig, AccessLogger access, SecurityBiz securityBiz, String dubboVersion){
        this.apiMetaDataManager = apiMetaDataManager;
        this.apiParamManager = apiParamManager;
        this.apiConfig = apiConfig;
        this.access = access;
        this.securityBiz = securityBiz;
        this.dubboVersion = dubboVersion;
    }

    AbstractReturnCode process(final ApiContext context, final HttpServletRequest request){

        String nameString = request.getParameter(CommonParameter.method);

        if (nameString != null && nameString.length() > 0) {

            String[] names = nameString.split(",");

            context.apiCallInfos = new ArrayList<ApiMethodCall>(names.length);

            for (int m = 0; m < names.length; m++) {

                String[] apiGroupName = names[m].split("\\.");

                ApiMetaData apiMetaData = apiMetaDataManager.getMobileApiByGroupNameVersion(apiGroupName[0], apiGroupName[1], this.dubboVersion);

                if (apiMetaData != null) {
                    if(ApiOpenState.INTERNAL.equals(apiMetaData.getStatus()) && !isInternalIP(context)) {
                        return ApiReturnCode.IP_DENIED;
                    }
                    ApiMethodCall call = new ApiMethodCall(apiMetaData);

                    List<ApiParam> apiParams= apiParamManager.listInputParamByApiId(apiMetaData.getApiId(),apiMetaData.getApiGroup());

                    call.returnTypeName = apiParams.get(0).getParamType();

                    int apiInputParamsSize = apiParams.size()-1;
                    String[] parameters = new String[apiInputParamsSize];
                    String[] parameterNames = new String[apiInputParamsSize];
                    String[] parameterTypes = new String[apiInputParamsSize];
                    String[] parameterTypeJsons = new String[apiInputParamsSize];
                    context.requiredSecurity = apiMetaData.getSecurityLevel() | context.requiredSecurity;

                    for( int i = 0; i < parameters.length; i++) {
                        ApiParam ap = apiParams.get(i+1);
                        parameterTypes[i] = ap.getParamType();
                        parameterNames[i] = ap.getParamName();
                        parameterTypeJsons[i] = ap.getParamTypeDetail();

                        if(ap.isAuto()) {
                            if (CommonParameter.userId.equals(ap.getParamName())) {
                                parameters[i] = context.caller == null ? "0" : String.valueOf(context.caller.uid);
                            } else if (CommonParameter.companyId.equals(ap.getParamName())) {
                                parameters[i] = context.caller == null ? "0" : String.valueOf(context.caller.companyId);}
                            else if (CommonParameter.deviceId.equals(ap.getParamName())) {
                                parameters[i] = context.caller == null ? "0" : String.valueOf(context.caller.deviceId);
                            } else if (CommonParameter.applicationId.equals(ap.getParamName())) {
                                parameters[i] = context.caller == null ? String.valueOf(context.appid) :
                                        ((SECURITY_WITH_USER_TK & apiMetaData.getSecurityLevel()) == apiMetaData.getSecurityLevel()) ?
                                                String.valueOf(context.caller.appid) : String.valueOf(context.appid);                          } else if (CommonParameter.token.equals(ap.getParamName())) {
                                parameters[i] = context.caller == null ? null : context.token;
                            } else if (CommonParameter.clientIp.equals(ap.getParamName())) {
                                parameters[i] = context.clientIP == null ? null : context.clientIP;
                            } else if (CommonParameter.domainId.equals(ap.getParamName())) {
                                parameters[i] = context.caller == null ? null : String.valueOf(context.caller.domainId);
                            } else if (CommonParameter.userAgent.equals(ap.getParamName())) {
                                parameters[i] = context.agent == null ? null : context.agent;
                            }

                            if (parameters[i] == null && MiscUtil.isPrimitive(ap.getParamType())) {
                                parameters[i] = "0";
                            }

                        } else {
                            if (names.length == 1) {
                                parameters[i] = request.getParameter(ap.getParamName());
                            } else {
                                String name = m + "_" + ap.getParamName();
                                parameters[i] = request.getParameter(name);
                            }

                            if(parameters[i]==null) {
                                parameters[i] = ap.getDefaultValue();
                            }

                            if (ap.isRequired() && parameters[i] == null) {
                                access.logRequest("with  error", "parameter validation failed, required parameter:"+ap.getParamName()+" is null");
                                return ApiReturnCode.PARAMETER_ERROR;
                            }


                            call.message.append(ap.getParamName()).append('=').append(parameters[i]).append('&');

                            if (ap.isRsaEncrypted() && (ap.isRequired() || parameters[i] != null)) {
                                try {
                                    RsaHelper rsaHelper = new RsaHelper(apiConfig.getApiRsaPublic(), apiConfig.getApiRsaPrivate());
                                    parameters[i] = new String(rsaHelper.decrypt(Base64Util.decode(parameters[i])), ConstField.UTF8);
                                } catch (Exception e) {
                                    return ApiReturnCode.PARAMETER_ERROR;
                                }
                            } else if (ap.isAesEncrypted() && (ap.isRequired() || parameters[i] != null)) {
                                try {
                                    AesHelper aesHelper = new AesHelper(Base64Util.decode(apiConfig.getApiTokenAes()), null);
                                    parameters[i] = new String(aesHelper.decrypt(Base64Util.decode(parameters[i])),
                                            ConstField.UTF8);
                                } catch (Exception e) {
                                    return ApiReturnCode.PARAMETER_ERROR;
                                }
                            }
                        }
                    }
                    call.parameterTypes = parameterTypes;
                    call.parameters = parameters;
                    call.parameterNames = parameterNames;
                    call.parameterTypeJsons = parameterTypeJsons;
                    if(parameterTypes.length != parameters.length) {
                        logger.error("The size of parameters should be {}, not {}", parameterTypes.length, parameters.length);
                        return ApiReturnCode.PARAMETER_ERROR;
                    }
                    context.apiCallInfos.add(call);
                } else {
                    String[] groupName = names[m].split("\\.");
                    logger.error("Cannot Found ApiMetaData for the method with group: {}, name: {}, version: {}", groupName[0], groupName[1], this.dubboVersion);
                    return ApiReturnCode.UNKNOWN_METHOD;
                }
            }

            if (!SecurityType.isNone(context.requiredSecurity)) {
                if (context.caller == null) {
                    return ApiReturnCode.TOKEN_ERROR;
                }
            }

            if (!checkSignature(context, context.requiredSecurity, request)) {
                return ApiReturnCode.SIGNATURE_ERROR;
            }

            return checkAuthorization(context, context.requiredSecurity);
        }
        return ApiReturnCode.REQUEST_PARSE_ERROR;

    }

    private boolean isInternalIP(ApiContext context) {
        String ip = context.clientIP;
        if (ip != null) {
            List<String> ipList = this.apiConfig.getAuthorizedIPs();
            if (ipList != null) {
                for (String authIp : ipList) {
                    if (ip.startsWith(authIp)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    private boolean checkSignature(ApiContext context, int securityLevel, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder(256);
        {
            List<String> list = new ArrayList<String>(10);
            Enumeration<String> keys = request.getParameterNames();
            while (keys.hasMoreElements()) {
                list.add(keys.nextElement());
            }
            String[] array = list.toArray(new String[list.size()]);
            if (array.length > 0) {
                Arrays.sort(array, MiscUtil.StringComparator);
                for (String key : array) {
                    if (CommonParameter.signature.equals(key)) {
                        continue;
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(request.getParameter(key));
                }
            }
        }
        String sig = request.getParameter(CommonParameter.signature);
        if (sig != null && sig.length() > 0) {
            if (SecurityType.isNone(securityLevel)) {
                byte[] expect = HexStringUtil.toByteArray(sig);
                byte[] actual = Md5Util.compute(sb.append(apiConfig.getStaticSignPwd()).toString().getBytes(ConstField.UTF8));
                return Arrays.equals(expect, actual);
            } else if (context.caller != null) {
                sb.append(Md5Util.computeToHex(
                        (context.token + apiConfig.getCsrfTokenSecret()).getBytes(ConstField.UTF8)));
                return Arrays.equals(HexStringUtil.toByteArray(sig), Md5Util.compute(sb.toString().getBytes(ConstField.UTF8)));
            } else {
                return false;
            }
        }
        return false;
    }

    private AbstractReturnCode checkAuthorization(ApiContext context, int authTarget) {
        if (SecurityType.isNone(authTarget)) {
            return ApiReturnCode.SUCCESS;
        }

        boolean securityServiceErrorOccured = false;

        int userDeviceBindingState = 0;

        if (SecurityType.UserLogin.check(authTarget)) {
            int checkUser = (context.caller != null && context.caller.uid != 0) ? 1 : -1;
            if (checkUser < 0) {
                return ApiReturnCode.USER_CHECK_FAILED;
            }
            try {
                userDeviceBindingState = securityBiz.checkUserStatus(context.clientIP, context.caller.appid, context.caller.deviceId, context.caller.uid);
            } catch (Throwable throwable) {
                logger.error("invoke security service failed", throwable);
                securityServiceErrorOccured = true;
            } finally {
                DubboExtProperty.clearNotificaitons();
            }
            if (securityServiceErrorOccured) {
                return ApiReturnCode.SECURITY_SERVICE_ERROR;
            }
            switch (userDeviceBindingState) {
                case 0:
                    break;
                case 4:
                    return ApiReturnCode.NO_TRUSTED_DEVICE;
                case 2:
                    return ApiReturnCode.NO_ACTIVE_DEVICE;
                case 1:
                    return ApiReturnCode.USER_CHECK_FAILED;
                case 3:
                    return ApiReturnCode.SESSION_EXPIRED;
                case 5:
                    return ApiReturnCode.IP_CHANGED;
                default:
                    return ApiReturnCode.ACCESS_DENIED;
            }
        }
        return ApiReturnCode.SUCCESS;
    }
}
