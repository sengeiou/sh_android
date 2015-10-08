package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;

public interface ProfileView {

    void showListingButtonWithCount(Integer listingCount);

    void navigateToListing(String idUser);

    void showLogoutInProgress();

    void showError();

    void hideLogoutInProgress();

    void navigateToWelcomeScreen();

    void showLogoutButton();

    void showSupportButton();

    void showChangePasswordButton();

    void showOpenStream();

    void navigateToCreatedStreamDetail(String streamId);

    void showShotShared();

    void setFollowing(Boolean following);

    void showListingWithoutCount();

    void setUserInfo(UserModel userModel);

    void showAllShots();

    void hideAllShots();

    void showError(String messageForError);

    void showEditProfileButton();

    void showFollowButton();

    void showUnfollowButton();

    void showAddPhoto();
}
