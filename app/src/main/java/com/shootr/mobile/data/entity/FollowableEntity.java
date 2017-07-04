package com.shootr.mobile.data.entity;

public class FollowableEntity extends Synchronized {

  private String followableType;

  public String getFollowableType() {
    return followableType;
  }

  public void setFollowableType(String followableType) {
    this.followableType = followableType;
  }
}
