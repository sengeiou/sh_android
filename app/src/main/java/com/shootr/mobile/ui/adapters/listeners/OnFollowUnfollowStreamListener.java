package com.shootr.mobile.ui.adapters.listeners;

import com.shootr.mobile.ui.model.StreamModel;

public interface OnFollowUnfollowStreamListener {

    void onFollow(StreamModel stream);

    void onUnfollow(StreamModel stream);
}
