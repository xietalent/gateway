package com.hrocloud.apigw.utils;

import com.hrocloud.apigw.client.define.CommonParameter;
import com.hrocloud.apigw.client.define.ConstField;
import com.hrocloud.apigw.client.utils.HexStringUtil;
import com.hrocloud.apigw.client.utils.Md5Util;
import com.hrocloud.apigw.client.utils.MiscUtil;

import java.util.*;

/**
 * Created by hanzhihua on 2016/11/20.
 */
public class SignUtil {

    public static String md5SignForNoneSecurityType(String orgUrl) throws Exception{
        return md5SignForNoneSecurityType(orgUrl,"www.hrocloud.com");
    }

    public static String md5SignForNoneSecurityType(String orgUrl,  String staticPwd) throws Exception {
        StringBuilder sb = parseUrlParam(orgUrl);
        byte[] actual = Md5Util.compute(sb.append(staticPwd).toString().getBytes(ConstField.UTF8));
        String sig = HexStringUtil.toHexString(actual);
        return sig;
    }

    public static String md5Sign(String orgUrl, String userToken) throws Exception{
        return md5Sign(orgUrl,userToken,"wtk.hrocloud.com");
    }

    public static String md5Sign(String orgUrl, String userToken, String csrfTokenSecret) throws Exception {
        StringBuilder sb = parseUrlParam(orgUrl);
        if(userToken==null){
            userToken = "";
        }
        System.out.println("wtk:"+Md5Util.computeToHex((userToken + csrfTokenSecret).getBytes(ConstField.UTF8)));
        sb.append(Md5Util.computeToHex((userToken + csrfTokenSecret).getBytes(ConstField.UTF8)));
        String sig = HexStringUtil.toHexString(Md5Util.compute(sb.toString().getBytes(ConstField.UTF8)));
        return sig;
    }

    public static StringBuilder parseUrlParam(String orgUrl) {
        String[] pairs = orgUrl.split("&");
        Map<String, String> kvMap = new HashMap<String, String>();
        List<String> list = new ArrayList<String>(10);
        for (String s1 : pairs) {
            String akey = s1.split("=")[0];
            String aValue = s1.substring(s1.indexOf("=") + 1, s1.length());
            list.add(akey);
            kvMap.put(akey, aValue);
        }

        StringBuilder sb = new StringBuilder(128);
        {
            String[] array = list.toArray(new String[list.size()]);
            if (array.length > 0) {
                Arrays.sort(array, MiscUtil.StringComparator);
                for (String key : array) {
                    if (CommonParameter.signature.equals(key)) {
                        continue;
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(kvMap.get(key));
                }
            }
        }
        return sb;
    }

    public static void main(String[] args) throws Exception{
        System.out.println(md5SignForNoneSecurityType("_mt=usermgmt.login&companyCode=HROCloud&applicationId=0&userna7a9c6b9a60a85387939451f7bd27dc4ame=ROOT&password=c33367701511b4f6020ec61ded352059","www.hrocloud.com"));
        System.out.println(md5SignForNoneSecurityType("_mt=usermgmt.logout","9437df3180f0165f94d95716ad824113"));

        System.out.println(md5Sign("_mt=user.getusername","5u8szAqRVVmNQbHSEuqEoQKu1Q++xL5RGrrKIKNh1m/QkLI1wX13GR5acJ07gvPpboLE+n8dDe5dVN7bcSWKBg=="));

    }
}
