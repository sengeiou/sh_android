package com.shootr.mobile.data.entity;

public class SearchItemEntity {

  private String type;
  private StreamEntity stream;
  private UserEntity user;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public StreamEntity getStreamEntity() {
    return stream;
  }

  public void setStreamEntity(StreamEntity streamEntity) {
    this.stream = streamEntity;
  }

  public UserEntity getUserEntity() {
    return user;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.user = userEntity;
  }
}
