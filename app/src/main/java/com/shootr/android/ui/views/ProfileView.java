package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.UserModel;
import java.util.List;

public interface ProfileView {

    void showListingButtonWithCount(Integer listingCount);

    void navigateToListing(String idUser, boolean isCurrentUser);

    void showLogoutInProgress();

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

    void showAllShotsButton();

    void hideAllShotsButton();

    void showError(String messageForError);

    void showEditProfileButton();

    void showFollowButton();

    void showUnfollowButton();

    void showAddPhoto();

    void openPhoto(String photo);

    void openEditPhotoMenu(boolean showRemove);

    void goToWebsite(String website);

    void goToFollowersList(String idUser);

    void goToFollowingList(String idUser);

    void renderLastShots(List<ShotModel> shots);

    void showUnfollowConfirmation(String username);

    void goToAllShots(String idUser);

    void showLatestShots();

    void hideLatestShots();

    void showLatestShotsEmpty();

    void hideLatestShotsEmpty();

    void showLoadingPhoto();

    void hideLoadingPhoto();
}
