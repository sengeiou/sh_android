package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.UserModel;

public interface ActivityFollowUnfollowListener {

  void onFollow(String idUser, String username);

  void onUnfollow(String idUser);
}
