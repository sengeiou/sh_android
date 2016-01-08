package com.shootr.mobile.data.api.entity;

public class FacebookLoginApiEntity {

    private String accessToken;
    private String language;

    public FacebookLoginApiEntity(String accessToken, String language) {
        this.accessToken = accessToken;
        this.language = language;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
