package com.hrocloud.apigw.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hanzhihua on 2016/11/21.
 */
public class HexString {

    public static String toString(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(0xFF & data[i]);
            if (hex.length() == 1) {
                buffer.append('0');
            }
            buffer.append(hex);
        }
        return buffer.toString();
    }

    public static byte[] toBytes(String s) {
        if (s == null) {
            return null;
        }

        if (s.equals("")) {
            return new byte[0];
        }

        Pattern pattern = Pattern.compile("[0-9a-fA-F]+");
        Matcher match = pattern.matcher(s);
        if (!match.matches()) {
            throw new IllegalArgumentException("toBytes: input is NOT in [0-9a-fA-F]+, s=[" + s + "]");
        }

        if ((s.length() % 2) != 0) {
            throw new IllegalArgumentException("toBytes: input is NOT in even, s=[" + s + "]");
        }

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
