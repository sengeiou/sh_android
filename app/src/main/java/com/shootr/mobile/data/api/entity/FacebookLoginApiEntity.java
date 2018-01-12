package com.shootr.mobile.data.api.entity;

public class FacebookLoginApiEntity {

  private String accessToken;
  private String locale;
  private String deviceUUID;
  private String advertisingId;

  public FacebookLoginApiEntity(String accessToken, String language, String advertisingId,
      String deviceUUID) {
    this.accessToken = accessToken;
    this.locale = language;
    this.advertisingId = advertisingId;
    this.deviceUUID = deviceUUID;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

}
