package com.shootr.mobile.data.entity;

public class OnBoardingStreamEntity {

  private StreamEntity stream;
  private boolean defaultStatus;

  public StreamEntity getStreamEntity() {
    return stream;
  }

  public void setStreamEntity(StreamEntity streamEntity) {
    this.stream = streamEntity;
  }

  public boolean isFavorite() {
    return defaultStatus;
  }

  public void setFavorite(boolean favorite) {
    this.defaultStatus = favorite;
  }
}
