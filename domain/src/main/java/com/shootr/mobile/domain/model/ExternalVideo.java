package com.shootr.mobile.domain.model;


public class ExternalVideo implements PrintableItem {

  private String provider;
  private String videoId;

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getVideoId() {
    return videoId;
  }

  public void setVideoId(String videoId) {
    this.videoId = videoId;
  }

  @Override public String getResultType() {
    return PrintableType.EXTERNAL_VIDEO;
  }

  @Override public Long getOrder() {
    return 0L;
  }

  @Override public String getMessageType() {
    return null;
  }
}
