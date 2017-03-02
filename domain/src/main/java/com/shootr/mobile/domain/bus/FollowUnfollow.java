package com.shootr.mobile.domain.bus;

public interface FollowUnfollow {

  interface Receiver {

    void onFollowUnfollow(FollowUnfollow.Event event);
  }

  class Event {
    private String idUser;
    private boolean isFollow;

    public Event(String idUser, boolean isFollowing) {
      this.idUser = idUser;
      this.isFollow = isFollowing;
    }

    public String getIdUser() {
      return idUser;
    }

    public void setIdUser(String idUser) {
      this.idUser = idUser;
    }

    public boolean isFollow() {
      return isFollow;
    }

    public void setFollow(boolean follow) {
      isFollow = follow;
    }
  }
}
