package com.shootr.android.data.api.entity;

public class FacebookLoginApiEntity {

    private String accessToken;

    public FacebookLoginApiEntity(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
