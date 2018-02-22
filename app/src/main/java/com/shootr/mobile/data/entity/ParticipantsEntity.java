package com.shootr.mobile.data.entity;

public class ParticipantsEntity {

  private int totalParticipants;
  private  int followingParticipants;

  public int getTotal() {
    return totalParticipants;
  }

  public void setTotal(int total) {
    this.totalParticipants = total;
  }

  public int getFollowing() {
    return followingParticipants;
  }

  public void setFollowing(int following) {
    this.followingParticipants = following;
  }
}
