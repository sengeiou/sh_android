package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class BootstrapingEntity {

  private SocketEntity socket;
  private ArrayList<FeatureFlagEntity> featureFlags;
  private String logsUrl;

  public SocketEntity getSocket() {
    return socket;
  }

  public void setSocket(SocketEntity socket) {
    this.socket = socket;
  }

  public ArrayList<FeatureFlagEntity> getFeatureFlags() {
    return featureFlags;
  }

  public void setFeatureFlags(ArrayList<FeatureFlagEntity> featureFlags) {
    this.featureFlags = featureFlags;
  }

  public String getLogsUrl() {
    return logsUrl;
  }

  public void setLogsUrl(String logsUrl) {
    this.logsUrl = logsUrl;
  }
}
