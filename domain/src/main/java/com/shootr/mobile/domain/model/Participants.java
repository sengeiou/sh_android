package com.shootr.mobile.domain.model;

public class Participants {

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
