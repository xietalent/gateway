package com.hrocloud.apigw.client.define;

import com.hrocloud.apigw.client.annoation.Description;

@Description("api通用参数参数")
public final class CommonParameter {

    @Description("company id 代表公司Id号")
    public static final String companyId="_comid";

    @Description("user token 代表访问者身份,完成用户登入流程后获取")
    public static final String token = "_tk";

    @Description("method 请求的资源名")
    public static final String method = "_mt";

    @Description("signature 参数字符串签名")
    public static final String signature = "_sig";

    @Description("application id 应用编号")
    public static final String applicationId = "_aid";

    @Description("call id 客户端调用编号")
    public static final String callId = "_cid";

    @Description("device id 设备标示符")
    public static final String deviceId = "_did";

    @Description("user id 用户标示符")
    public static final String userId = "_uid";

    @Description("client ip 用户ip")
    public static final String clientIp = "_cip";

    @Description("domain id 用户域id")
    public static final String domainId = "_domid";

    @Description("user agent 比如:ie、chrome")
    public static final String userAgent = "_ua";

}
