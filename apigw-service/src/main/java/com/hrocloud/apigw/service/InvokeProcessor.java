package com.hrocloud.apigw.service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.service.GenericException;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hrocloud.apigw.client.define.*;
import com.hrocloud.apigw.client.dubboext.DubboExtProperty;
import com.hrocloud.apigw.client.utils.Constants;
import com.hrocloud.apigw.client.utils.MiscUtil;
import com.hrocloud.apigw.exception.GatewayException;
import com.hrocloud.apigw.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hanzhihua on 2016/11/22.
 */
class InvokeProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    private HttpServletRequest request;

    private HttpServletResponse response;

    private AccessLogger access;

    InvokeProcessor(HttpServletRequest request,HttpServletResponse response,AccessLogger access){
        this.request = request;
        this.response = response;
        this.access = access;
    }

    public void process(final ApiContext apiContext)  throws SerializeException {

        int count = 0;

        for (ApiMethodCall call : apiContext.apiCallInfos) {

            apiContext.currentCall = call;

            call.startTime = (count++ == 0) ? apiContext.startTime : System.currentTimeMillis();

            executeApiCall(call, request, response, apiContext);

            call.costTime = (int)(System.currentTimeMillis() - call.startTime);

            serializeCallResult(apiContext, request, response, call);

            access.logAccess(call.costTime, call.apiMetaData.getMethodName(), call.getReturnCode().getCode(), call.getOriginCode().getCode(),
                    call.resultLen, call.message.toString());
        }
    }


    private void executeApiCall(ApiMethodCall call, HttpServletRequest request, HttpServletResponse response, ApiContext apiContext) {
        try {
            RpcContext.getContext().setAttachment(CommonParameter.callId, apiContext.cid);
            call.result = processApiCall(call, apiContext);
            Map<String, String> notifications = DubboExtProperty.getCurrentNotifications();
            if (notifications != null && notifications.size() > 0) {
                for (Map.Entry<String, String> entry : notifications.entrySet()) {
                    if (ConstField.SET_COOKIE_TOKEN.equals(entry.getKey())) {
                        Cookie tk_cookie = new Cookie(CommonParameter.token, URLEncoder.encode(entry.getValue(), "utf-8"));
                        tk_cookie.setMaxAge(-1);
                        tk_cookie.setHttpOnly(true);
                        tk_cookie.setPath("/");
			//tk_cookie.setDomain(".hrocloud.com");
                        response.addCookie(tk_cookie);
                    } else if (ConstField.ERROR_CODE.equals(entry.getKey())) {
                        AbstractReturnCode rcCode = ReturnCodeContainer.getCode(Integer.parseInt(entry.getValue()));
                        if (rcCode==null) {
                            call.setReturnCode(new ApiReturnCode(null, Integer.parseInt(entry.getValue())));
                        }else {
                            call.setReturnCode(rcCode);
                        }
                    }
                }
            }

            call.setReturnCode(ApiReturnCode.SUCCESS);
        } catch (ReturnCodeException rce) {
            call.setReturnCode(rce.getCode());
            logger.error("Exception:"+ApiContext.getCurrent().appid+"."+call.apiMetaData.getMethodName(), rce);
        } catch (Throwable t) {
            if (t.getCause() instanceof com.alibaba.dubbo.remoting.TimeoutException) {
                logger.error("Exception:"+ApiContext.getCurrent().appid+"."+call.apiMetaData.getMethodName(), t);
                call.setReturnCode(ReturnCodeContainer.findCode(ApiReturnCode.DUBBO_SERVICE_TIMEOUT_ERROR.getCode()));
            } else if (t.getCause() instanceof com.alibaba.dubbo.remoting.RemotingException || t.getCause() instanceof com.alibaba.dubbo.rpc.RpcException) {
                logger.error("Exception:"+ApiContext.getCurrent().appid+"."+call.apiMetaData.getMethodName(), t);
                call.setReturnCode(ReturnCodeContainer.findCode(ApiReturnCode.DUBBO_SERVICE_NOTFOUND_ERROR.getCode()));
            } else if (t instanceof java.lang.IllegalStateException && t.getMessage().contains("No provider available for the service") ) {
                logger.error("Exception:"+ApiContext.getCurrent().appid+"."+call.apiMetaData.getMethodName(), t);
                call.setReturnCode(ReturnCodeContainer.findCode(ApiReturnCode.DUBBO_SERVICE_NOTFOUND_ERROR.getCode()));
            } else if (t instanceof com.alibaba.dubbo.rpc.RpcException) {
                logger.error("Exception:"+ApiContext.getCurrent().appid+"."+call.apiMetaData.getMethodName(), t);
                call.setReturnCode(ReturnCodeContainer.findCode(ApiReturnCode.DUBBO_SERVICE_NOTFOUND_ERROR.getCode()));
            } else if (t instanceof GenericException) {
                call.setReturnCode(ReturnCodeContainer.findCode(ApiReturnCode.INTERNAL_SERVER_ERROR.getCode()));
            } else {
                call.setReturnCode(ReturnCodeContainer.findCode(ApiReturnCode.INTERNAL_SERVER_ERROR.getCode()));
                logger.error("internal error.", t);
            }

        } finally {
            DubboExtProperty.clearNotificaitons();
        }

        AbstractReturnCode code = call.getReturnCode();
        AbstractReturnCode display = code.getDisplay();

        if (display.getCode() > 0) {
            if (call.apiMetaData.getErrors().length==0) {
                call.replaceReturnCode(ApiReturnCode.UNKNOWN_ERROR);
            } else {
                if (Arrays.binarySearch(call.apiMetaData.getErrors(), display.getCode()) < 0) {
                    call.replaceReturnCode(ApiReturnCode.UNKNOWN_ERROR);
                }
            }
        }
        if (code != display) {
            call.replaceReturnCode(display);
        }
    }


    private Object processApiCall(ApiMethodCall call, ApiContext apiContext) throws GatewayException {
        try {
            GenericService genericService = DubboUtil.getService(DubboUtil.getApiInterface(call.apiMetaData.getInvokeInterface()),call.apiMetaData.getVersion());
            Object[] actualParams = new Object[call.parameters.length];
            for(int i = 0; i < actualParams.length; i++) {
                try {
                    if (call.parameterTypes[i].endsWith(Constants.ARRAY_TYPE_SUFFIX)) {
                        if (MiscUtil.isPrimitiveArrayType(call.parameterTypes[i])) {
                            call.parameters[i] = MiscUtil.checkRevisePrimitiveArrayParam(call.parameterNames[i], call.parameters[i], call.parameterTypes[i]);
                        } else if (call.parameterTypeJsons[i] != null){
                            call.parameters[i] = MiscUtil.checkReviseObjArrayParam(call.parameterNames[i], call.parameters[i], call.parameterTypes[i], call.parameterTypeJsons[i]);
                        }

                        JSONArray jsonArray = JSON.parseArray(call.parameters[i]);
                        if (jsonArray != null) {
                            actualParams[i] = jsonArray.toArray();
                        }
                    } else {
                        actualParams[i] = MiscUtil.convertParamType(call.parameterNames[i], call.parameters[i], call.parameterTypes[i], call.parameterTypeJsons[i]);
                    }
                    if (call.parameterTypes[i].startsWith(Constants.LIST_TYPE_NAME)) {
                        call.parameterTypes[i] = Constants.LIST_TYPE_NAME;
                    }
                } catch (ReturnCodeException returnCodeException) {
                    throw returnCodeException;
                } catch (Exception e) {
                    throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : "+ call.parameterNames[i] + "=" + call.parameters[i], e);
                }
            }
            Object invokedResult = genericService.$invoke(call.apiMetaData.getInvokeMethod(), call.parameterTypes, actualParams);
            int costTime = (int) (System.currentTimeMillis() - apiContext.startTime);

            if (invokedResult != null)
                logger.debug("invokedResult content to String = [{}]", invokedResult.toString());

            return invokedResult;
        } catch (RpcException e) {
            throw e;
        } catch (GenericException e) {
            logger.error("troyException:"+ApiContext.getCurrent().appid+"."+call.apiMetaData.getMethodName(), e);
            throw e;
        } catch (Throwable e) {
            if (e instanceof GatewayException) {
                throw (GatewayException) e;
            }
            if (e.getCause() != null && e.getCause() instanceof GatewayException) {
                throw (GatewayException) e.getCause();
            }
            throw e;
        }

    }









    private void serializeCallResult(ApiContext apiContext, HttpServletRequest request, HttpServletResponse response,
                                     ApiMethodCall call) throws SerializeException {
        try {
            int oldSize = apiContext.outputStream.size();
            if (apiContext.serializeCount > 0) {
                apiContext.outputStream.write(Constants.JSON_SPLIT);
            }
            if (call.result == null) {
                apiContext.outputStream.write(Constants.JSON_EMPTY);
            } else {
                Object result = call.result;
                if (result != null && result instanceof  HashMap) {
                    if (Constants.JSON_STRING_TYPE_NAME.equals(((HashMap)result).get(Constants.CLASS_NAME_STR))) {
                        String valueStr  = (String)((HashMap)result).get(Constants.RESULT_KEY);
                        if (valueStr != null) {
                            result = JSON.parseObject(valueStr, Map.class);
                        }else {
                            call.resultLen = apiContext.outputStream.size() - oldSize;
                        }
                    } else if (Constants.RAW_STRING_TYPE_NAME.equals(((HashMap)result).get(Constants.CLASS_NAME_STR))) {
                        String valueStr  = (String)((HashMap)result).get(Constants.RESULT_KEY);
                        if (valueStr != null) {
                            apiContext.outputStream.write(valueStr.getBytes(ConstField.UTF8));
                        }
                        call.resultLen = apiContext.outputStream.size() - oldSize;
                    }
                }
                if (result != null && result instanceof List) {
                    if (((List)result).isEmpty()) {
                        if (Constants.LIST_STRING_TYPE.equals(call.returnTypeName)) {
                            apiContext.outputStream.write(Constants.LIST_STRING_EMPTY_RESULT);
                        } else {
                            apiContext.outputStream.write(Constants.JSON_EMPTY);
                        }
                        call.resultLen = apiContext.outputStream.size() - oldSize;
                    }
                }

                Object ret = DubboBeanCodecUtil.generalize(result);

                if(isNeedValueKey(ret)) {
                    Map resultMap = new HashMap<String, Object>();
                    resultMap.put(Constants.RESULT_KEY, ret);
                    ret = resultMap;
                }
                MiscUtil.removeClassName(ret);

                String resultJsonStr = JSON.toJSONString(ret, new SerializerFeature[]{});
                apiContext.outputStream.write(resultJsonStr.getBytes(ConstField.UTF8));
            }
            call.resultLen = apiContext.outputStream.size() - oldSize;
        } catch (Exception e) {
            throw new SerializeException(e);
        } finally {
            apiContext.serializeCount++;
        }
    }

    private boolean isNeedValueKey(Object result) {
        if (result instanceof Boolean) {
            return true;
        } else if (result instanceof boolean[]) {
            return true;
        } else if (result instanceof Double || result instanceof Float) {
            return true;
        } else if (result instanceof double[] || result instanceof float[]) {
            return true;
        } else if (result instanceof Long) {
            return true;
        } else if (result instanceof long[]) {
            return true;
        } else if (result instanceof Integer || result instanceof Byte || result instanceof Short) {
            return true;
        } else if (result instanceof int[] || result instanceof byte[] || result instanceof short[]) {
            return true;
        } else if (result instanceof Object[]) {
            return true;
        } else if (result instanceof String) {
            return true;
        } else if (result instanceof Character) {
            return true;
        } else if (result instanceof char[]) {
            return true;
        } else if (result instanceof List) {
            return true;
        }
        return false;
    }





}
