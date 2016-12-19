package com.hrocloud.apigw.client.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.hrocloud.apigw.client.define.CallerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESTokenHelper {
    private static final Logger logger                   = LoggerFactory.getLogger(AESTokenHelper.class);
    private static final short  DEVICE_TOKEN_VERSION_1_0 = 10;
    private static final String VALUE_SPLIT_REGEX        = "\\|";
    private AesHelper aes;
    public AESTokenHelper(String pwd) {
        aes = new AesHelper(Base64Util.decode(pwd), null);
    }

    public AESTokenHelper(AesHelper helper) {aes = helper;}

    public CallerInfo parseUserToken(String token) {
        try {
            return parseUserToken(Base64Util.decode(token));
        } catch (Exception e) {
            logger.error("token parse failed.", e);
        }
        return null;
    }

    public CallerInfo parseUserToken(byte[] token) {
        CallerInfo caller = null;
        try {
            String[] sourceData = new String(aes.decrypt(token), "UTF-8").split(VALUE_SPLIT_REGEX);
            caller = new CallerInfo();
            if (sourceData.length < 7) {
                logger.error("invalid token,source data is not enough!");
                return null;
            }

            if (sourceData[0] != null && !sourceData[0].isEmpty()) {
                caller.domainId = Long.parseLong(sourceData[0].trim());
            }
            if (sourceData[1] != null && !sourceData[1].isEmpty()) {
                caller.companyId = Long.parseLong(sourceData[1].trim());
            }
            if (sourceData[2] != null && !sourceData[2].isEmpty()) {
                caller.uid = Long.parseLong(sourceData[2].trim());
            }
            if (sourceData[4] != null && !sourceData[4].isEmpty()) {
                caller.appid = Integer.parseInt(sourceData[4].trim());
            }
            if (sourceData[5] != null && !sourceData[5].isEmpty()) {
                caller.expire = Long.parseLong(sourceData[5].trim());
            }
            if (sourceData[6] != null && !sourceData[6].isEmpty()) {
                caller.deviceId = Long.parseLong(sourceData[6].trim());
            }
        } catch (Exception e) {
            logger.error("token parse failed.", e);
        }
        return caller;
    }
}
