package com.shootr.mobile.data.entity;

import com.shootr.mobile.domain.model.PrintableType;
import javax.inject.Inject;

public class ExternalVideoEntity implements PrintableItemEntity {

  private String provider;
  private String videoId;

  @Inject public ExternalVideoEntity() {
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

  @Override public String getResultType() {
    return PrintableType.EXTERNAL_VIDEO;
  }
}
