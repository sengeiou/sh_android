package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.domain.model.PrintableType;

public class ExternalVideoApiEntity extends PrintableItemApiEntity {

  private String provider;
  private String videoId;

  public ExternalVideoApiEntity() {
    setResultType(PrintableType.EXTERNAL_VIDEO);
  }

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
}
