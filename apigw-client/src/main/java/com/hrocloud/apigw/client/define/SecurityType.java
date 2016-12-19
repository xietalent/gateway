package com.hrocloud.apigw.client.define;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SecurityType {

    None(0x00, false),

    UserLogin(0x2000, true);

    private int     code;
    private boolean needUserToken;
    private static final Logger logger = LoggerFactory.getLogger(SecurityType.class);

    private SecurityType(int code, boolean needUserToken) {
        this.code = code;
        this.needUserToken = needUserToken;
    }

    public boolean check(int auth) {
        return (auth & code) == code;
    }

    public boolean check(SecurityType auth) {return (auth.code & code) == code;}

    public int authorize(int auth) {
    	logger.debug("do authorize,authorize:{},org:{},result:{}", this, auth, auth | this.code);
     
        return auth | this.code;
    }

    public static boolean isNone(int auth) {
        return auth == 0;
    }

    public boolean isNeedUserToken() {
        return needUserToken;
    }

    public SecurityType valueOf(int code) {
        for( SecurityType st : SecurityType.values()) {
            if (code == st.code) return st;
        }

        return null;
    }

    public int getCode() {
        return this.code;
    }
}
