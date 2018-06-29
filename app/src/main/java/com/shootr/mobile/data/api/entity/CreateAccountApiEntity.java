package com.shootr.mobile.data.api.entity;

public class CreateAccountApiEntity {

  private String userName;
  private String email;
  private String password;
  private String locale;
  private String deviceUUID;
  private String advertisingId;
  private boolean privacyPolicyAccepted;

  public CreateAccountApiEntity(String userName, String email, String password, String locale,
      String deviceUUID, String advertisingId, boolean privacyAccepted) {
    this.userName = userName;
    this.email = email;
    this.password = password;
    this.locale = locale;
    this.deviceUUID = deviceUUID;
    this.advertisingId = advertisingId;
    this.privacyPolicyAccepted = privacyAccepted;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public boolean getPrivacyPolicyAccepted() {
    return privacyPolicyAccepted;
  }

  public void setPrivacyPolicyAccepted(boolean privacyPolicyAccepted) {
    this.privacyPolicyAccepted = privacyPolicyAccepted;
  }
}
