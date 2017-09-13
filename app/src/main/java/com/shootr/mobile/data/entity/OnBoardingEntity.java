package com.shootr.mobile.data.entity;

public class OnBoardingEntity {

  private StreamEntity stream;
  private UserEntity userEntity;
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

  public UserEntity getUserEntity() {
    return userEntity;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }
}
