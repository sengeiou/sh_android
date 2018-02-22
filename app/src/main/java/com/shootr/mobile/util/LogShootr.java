package com.shootr.mobile.util;

import com.google.gson.Gson;

public class LogShootr {

  private String message;
  private long timestamp;
  private String app;
  private String platform;
  private String version;
  private String userName;
  private String networkType;
  private String networkStatus;
  private String model;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getNetworkType() {
    return networkType;
  }

  public void setNetworkType(String networkType) {
    this.networkType = networkType;
  }

  public String getNetworkStatus() {
    return networkStatus;
  }

  public void setNetworkStatus(String networkStatus) {
    this.networkStatus = networkStatus;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }
}
