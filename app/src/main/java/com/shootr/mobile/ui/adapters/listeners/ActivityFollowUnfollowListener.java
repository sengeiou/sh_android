package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.UserModel;

public interface ActivityFollowUnfollowListener {

  void onFollow(String idUser);

  void onUnfollow(String idUser);
}
