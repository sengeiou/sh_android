package com.shootr.mobile.data.entity;

public class SearchItemEntity {

  private String type;
  private StreamEntity streamEntity;
  private UserEntity userEntity;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public StreamEntity getStreamEntity() {
    return streamEntity;
  }

  public void setStreamEntity(StreamEntity streamEntity) {
    this.streamEntity = streamEntity;
  }

  public UserEntity getUserEntity() {
    return userEntity;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }
}
