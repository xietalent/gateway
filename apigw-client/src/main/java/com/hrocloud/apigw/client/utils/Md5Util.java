package com.hrocloud.apigw.client.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    public static final byte[] compute(byte[] content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String computeToHex(byte[] content) {
        return HexStringUtil.toHexString(compute(content));
    }

    public static final String computeToBase64(byte[] content) {
        return Base64Util.encodeToString(compute(content));
    }

    public static String md5HexString(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(value.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            // ignore
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        byte[] digest = md5.digest();
        return HexString.toString(digest);
    }

    public static String md5HexString(String... values) {
        if (values == null) {
            return null;
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            for (String value : values) {
                if (value != null && value.length() != 0) {
                    md5.update(value.getBytes("utf-8"));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            // ignore
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        byte[] digest = md5.digest();
        return HexString.toString(digest);
    }
}
