package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.UserFollowsView;
import java.util.List;

public class NullUserFollowsView implements UserFollowsView {

    @Override public void showError(String messageForError) {
        /* no-op */
    }

    @Override public void showUsers(List<UserModel> userModels) {
        /* no-op */
    }

    @Override public void setLoadingView(Boolean loading) {
        /* no-op */
    }

    @Override public void setEmpty(Boolean empty) {
        /* no-op */
    }

    @Override public void updateFollow(String idUser, Boolean following) {
        /* no-op */
    }

    @Override public void showUserBlockedError() {
        /* no-op */
    }

    @Override public void showNoFollowers() {
        /* no-op */
    }

    @Override public void showNoFollowing() {
        /* no-op */
    }

    @Override public void showProgressView() {
        /* no-op */
    }

    @Override public void hideProgressView() {
        /* no-op */
    }

    @Override public void renderUsersBelow(List<UserModel> olderUsers) {
        /* no-op */
    }

    @Override public void registerAnalytics(boolean followers) {
        /* no-op */
    }
}
