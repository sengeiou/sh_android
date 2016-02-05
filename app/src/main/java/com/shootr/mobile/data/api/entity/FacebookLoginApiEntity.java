package com.shootr.mobile.data.api.entity;

public class FacebookLoginApiEntity {

    private String accessToken;
    private String locale;

    public FacebookLoginApiEntity(String accessToken, String language) {
        this.accessToken = accessToken;
        this.locale = language;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
