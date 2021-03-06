package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;

public interface MainScreenView {

    void setUserData(UserModel userModel);

    void showActivityBadge(int count);

    void showConnectController(StreamModel streamModel);

    void hideConnectController();

    void goToTimeline(StreamModel streamModel);

    void updateChannelBadge(int unreadFollowChannels, boolean isFollowing);

    void initSocket();
}
