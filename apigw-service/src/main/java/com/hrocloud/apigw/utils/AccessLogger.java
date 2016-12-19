package com.hrocloud.apigw.utils;

import com.hrocloud.apigw.client.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public final class AccessLogger {

    private static String accessFileLoggerName ="com.hrocloud.apigw.access";

    private static       Logger              accessFileLogger          = LoggerFactory.getLogger(accessFileLoggerName);
    public static final  String              ACCESS_SPLITTER           = new String(new char[]{' ', 1});

    private static String serverAddress;

    static {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            serverAddress = Md5Util.computeToHex(addr.getHostAddress().getBytes("UTF-8")).substring(0, 6);
        } catch (Exception e) {
            accessFileLogger.error("can not get server address,_cid may be not unique", e);
        }

    }

    public String getServerAddress() {
        return serverAddress;
    }


    public void logRequest() {
        ApiContext apiContext = ApiContext.getCurrent();
        accessFileLogger.info(
                apiContext.requestInfo + ACCESS_SPLITTER + apiContext.agent + ACCESS_SPLITTER + apiContext.clientIP + ACCESS_SPLITTER + apiContext.token + ACCESS_SPLITTER);

    }

    public void logRequest(String errorMsg, String data) {
        ApiContext apiContext = ApiContext.getCurrent();
        if (StringUtils.isNotBlank(errorMsg)) {
            accessFileLogger.info(
                    apiContext.requestInfo + ACCESS_SPLITTER + apiContext.agent + ACCESS_SPLITTER + apiContext.clientIP + ACCESS_SPLITTER + apiContext.token + ACCESS_SPLITTER + errorMsg + ACCESS_SPLITTER + data);
        } else {
            accessFileLogger.info(
                    apiContext.requestInfo + ACCESS_SPLITTER + apiContext.agent + ACCESS_SPLITTER + apiContext.clientIP + ACCESS_SPLITTER + apiContext.token + ACCESS_SPLITTER + ACCESS_SPLITTER);

        }
    }

    public void logAccess(int costTime, String methodName, int returnCode, int orginReturnCode, int resultLen, String callMsg) {
        accessFileLogger.debug(
                costTime + ACCESS_SPLITTER + methodName + ACCESS_SPLITTER + returnCode + ACCESS_SPLITTER + orginReturnCode + ACCESS_SPLITTER + resultLen + ACCESS_SPLITTER + callMsg );
    }

}
