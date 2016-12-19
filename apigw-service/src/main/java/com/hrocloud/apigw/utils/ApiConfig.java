package com.hrocloud.apigw.utils;

import java.util.List;

public class ApiConfig {

    private List<String> authorizedIPs;

    private String serviceVersion;

    private String csrfTokenSecret;

    private String  apiTokenAes;

    private String apiRsaPublic;

    private String apiRsaPrivate;

    public List<String> getAuthorizedIPs() {
        return authorizedIPs;
    }

    public void setAuthorizedIPs(List<String> authorizedIPs) {
        this.authorizedIPs = authorizedIPs;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getCsrfTokenSecret() {
        return csrfTokenSecret;
    }

    public void setCsrfTokenSecret(String csrfTokenSecret) {
        this.csrfTokenSecret = csrfTokenSecret;
    }

    public String getApiTokenAes() {
        return apiTokenAes;
    }

    public void setApiTokenAes(String apiTokenAes) {
        this.apiTokenAes = apiTokenAes;
    }

    public String getApiRsaPublic() {
        return apiRsaPublic;
    }

    public void setApiRsaPublic(String apiRsaPublic) {
        this.apiRsaPublic = apiRsaPublic;
    }

    public String getApiRsaPrivate() {
        return apiRsaPrivate;
    }

    public void setApiRsaPrivate(String apiRsaPrivate) {
        this.apiRsaPrivate = apiRsaPrivate;
    }

    public String getStaticSignPwd() {
        return staticSignPwd;
    }

    public void setStaticSignPwd(String staticSignPwd) {
        this.staticSignPwd = staticSignPwd;
    }

    private String staticSignPwd;



}
