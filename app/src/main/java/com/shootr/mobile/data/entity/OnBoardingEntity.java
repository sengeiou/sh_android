package com.shootr.mobile.data.entity;

public class OnBoardingEntity {

  private StreamEntity stream;
  private UserEntity user;
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

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }
}
