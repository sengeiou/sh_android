package com.shootr.mobile.ui.adapters.listeners;

public interface ActivityFollowUnfollowListener {

  void onFollow(String idUser, String username);

  void onUnfollow(String idUser, String username);
}
