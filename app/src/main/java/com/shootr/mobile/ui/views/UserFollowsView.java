package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.UserModel;

import java.util.List;

public interface UserFollowsView {

    void showError(String messageForError);

    void showUsers(List<UserModel> userModels);

    void setLoadingView(Boolean loading);

    void setEmpty(Boolean empty);

    void updateFollow(String idUser, Boolean following);

    void showUserBlockedError();

    void showNoFollowers();

    void showNoFollowing();

    void showProgressView();

    void hideProgressView();

    void renderUsersBelow(List<UserModel> olderUsers);

    void registerAnalytics(boolean followers);
}
