package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.UserModel;

public interface OnFollowUnfollowListener {

    void onFollow(UserModel user);

    void onUnfollow(UserModel user);
}
