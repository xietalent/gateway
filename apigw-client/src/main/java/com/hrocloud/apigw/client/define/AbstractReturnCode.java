package com.hrocloud.apigw.client.define;

public abstract class AbstractReturnCode {

    private       String name;
    private final String desc;
    private final int    code;

    private       String             service;
    private final AbstractReturnCode display;

    public final static int                _C_SUCCESS = 0;
    public final static AbstractReturnCode SUCCESS    = new ApiReturnCode("成功", _C_SUCCESS);

    public final static int                _C_TOKEN_EXPIRE = -300;
    public final static AbstractReturnCode TOKEN_EXPIRE    = new ApiReturnCode("token已过期", _C_TOKEN_EXPIRE);

    public final static int                _C_NO_TRUSTED_DEVICE = -320;
    public final static AbstractReturnCode NO_TRUSTED_DEVICE    = new ApiReturnCode("不是用户的受信设备", _C_NO_TRUSTED_DEVICE);

    public final static int                _C_NO_ACTIVE_DEVICE = -340;
    public final static AbstractReturnCode NO_ACTIVE_DEVICE    = new ApiReturnCode("不是激活设备(用户在其他地方登录)", _C_NO_ACTIVE_DEVICE);

    public final static int                _C_TOKEN_ERROR = -360;
    public final static AbstractReturnCode TOKEN_ERROR    = new ApiReturnCode("token错误", _C_TOKEN_ERROR);

    public final static int                _C_USER_LOCKED = -370;
    public final static AbstractReturnCode USER_LOCKED    = new ApiReturnCode("用户被锁定", _C_USER_LOCKED);

    public final static int                _C_RISK_USER_LOCKED = -380;
    public final static AbstractReturnCode RISK_USER_LOCKED    = new ApiReturnCode("用户被锁定", _C_RISK_USER_LOCKED);


    public AbstractReturnCode(String desc, int code) {
        this.desc = desc;
        this.code = code;
        this.display = this;
    }

    public AbstractReturnCode(int code, AbstractReturnCode shadow) {
        this.desc = null;
        this.code = code;
        this.display = shadow;
    }

    public AbstractReturnCode(String name, int code, String desc) {
        this.name = name;
        this.desc = desc;
        this.code = code;
        this.display = this;
    }

    public String getDesc() {
        return desc;
    }
    public int getCode() {
        return code;
    }
    public AbstractReturnCode getDisplay() {
        return display;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }

}
