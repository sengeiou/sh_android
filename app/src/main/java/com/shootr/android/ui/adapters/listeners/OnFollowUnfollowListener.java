package com.shootr.android.ui.adapters.listeners;

import com.shootr.android.ui.model.UserModel;

public interface OnFollowUnfollowListener {

    void onFollow(UserModel user);

    void onUnfollow(UserModel user);
}
