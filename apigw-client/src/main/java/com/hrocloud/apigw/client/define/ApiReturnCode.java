package com.hrocloud.apigw.client.define;

public class ApiReturnCode extends AbstractReturnCode {

    public final static int _C_NO_ASSIGN = Integer.MIN_VALUE;
    public final static AbstractReturnCode NO_ASSIGN = new ApiReturnCode("未分配返回值", _C_NO_ASSIGN);

    public final static int _C_UNKNOWN_ERROR = -100;
    public final static AbstractReturnCode UNKNOWN_ERROR = new ApiReturnCode("服务端返回未知错误", _C_UNKNOWN_ERROR);

    private final static int _C_INTERNAL_SERVER_ERROR = -101;
    public final static AbstractReturnCode INTERNAL_SERVER_ERROR = new ApiReturnCode(_C_INTERNAL_SERVER_ERROR,
            ApiReturnCode.UNKNOWN_ERROR);

    private final static int _C_SERIALIZE_FAILED = -102;
    public final static AbstractReturnCode SERIALIZE_FAILED = new ApiReturnCode(_C_SERIALIZE_FAILED, UNKNOWN_ERROR);

    private final static int _C_IP_DENIED = -103;
    public final static AbstractReturnCode IP_DENIED = new ApiReturnCode(_C_IP_DENIED, UNKNOWN_ERROR);

    private final static int _C_FATAL_ERROR = -104;
    public final static AbstractReturnCode FATAL_ERROR = new ApiReturnCode(_C_FATAL_ERROR, UNKNOWN_ERROR);

    private final static int _C_WEB_ACCESS_FAILED = -105;
    public final static AbstractReturnCode WEB_ACCESS_FAILED = new ApiReturnCode(_C_WEB_ACCESS_FAILED,
            UNKNOWN_ERROR);

    private final static int _C_SECURITY_SERVICE_ERROR = -106;
    public final static AbstractReturnCode SECURITY_SERVICE_ERROR = new ApiReturnCode(_C_SECURITY_SERVICE_ERROR,
            UNKNOWN_ERROR);

    private final static int _C_DUBBO_SERVICE_NOTFOUND_ERROR = -107;
    public final static AbstractReturnCode DUBBO_SERVICE_NOTFOUND_ERROR = new ApiReturnCode(
            _C_DUBBO_SERVICE_NOTFOUND_ERROR, UNKNOWN_ERROR);

    private final static int _C_DUBBO_SERVICE_TIMEOUT_ERROR = -108;
    public final static AbstractReturnCode DUBBO_SERVICE_TIMEOUT_ERROR = new ApiReturnCode(
            _C_DUBBO_SERVICE_TIMEOUT_ERROR, UNKNOWN_ERROR);

    public final static int _C_UNKNOWN_METHOD = -120;
    public final static AbstractReturnCode UNKNOWN_METHOD = new ApiReturnCode("mt参数服务端无法识别", _C_UNKNOWN_METHOD);

    public final static int _C_PARAMETER_ERROR = -140;
    public final static AbstractReturnCode PARAMETER_ERROR = new ApiReturnCode("参数错误", _C_PARAMETER_ERROR);

    public final static int _C_ACCESS_DENIED = -160;
    public final static AbstractReturnCode ACCESS_DENIED = new ApiReturnCode("访问被拒绝", _C_ACCESS_DENIED);

    public final static int _C_IP_CHANGED = -162;
    public final static AbstractReturnCode IP_CHANGED = new ApiReturnCode("IP地址发生变化", _C_IP_CHANGED);

    public final static int _C_SESSION_EXPIRED = -163;
    public final static AbstractReturnCode SESSION_EXPIRED = new ApiReturnCode("会话已过期", _C_SESSION_EXPIRED);

    private final static int _C_USER_CHECK_FAILED = -161;
    public final static AbstractReturnCode USER_CHECK_FAILED = new ApiReturnCode(_C_USER_CHECK_FAILED,
            ACCESS_DENIED);

    public final static int _C_UNKNOW_TOKEN_DENIED = -164;
    public final static AbstractReturnCode UNKNOW_TOKEN_DENIED = new ApiReturnCode(_C_UNKNOW_TOKEN_DENIED,
            ACCESS_DENIED);

    public final static int _C_SIGNATURE_ERROR = -180;
    public final static AbstractReturnCode SIGNATURE_ERROR = new ApiReturnCode("签名错误", _C_SIGNATURE_ERROR);

    public final static int _C_REQUEST_PARSE_ERROR = -200;
    public final static AbstractReturnCode REQUEST_PARSE_ERROR = new ApiReturnCode("请求解析错误", _C_REQUEST_PARSE_ERROR);

    public final static int _C_API_UPGRADE = -220;
    public final static AbstractReturnCode API_UPGRADE = new ApiReturnCode("接口已升级", _C_API_UPGRADE);

    public final static int _C_APPID_NOT_EXIST = -280;
    public final static AbstractReturnCode APPID_NOT_EXIST = new ApiReturnCode("应用id不存在", _C_APPID_NOT_EXIST);

    public ApiReturnCode(String desc, int code) {
        super(desc, code);
    }

    public ApiReturnCode(int code, AbstractReturnCode display) {
        super(code, display);
    }

}
