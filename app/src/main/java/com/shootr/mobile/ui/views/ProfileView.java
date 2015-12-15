package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.UserModel;
import java.util.List;

public interface ProfileView {

    void navigateToListing(String idUser, boolean isCurrentUser);

    void showLogoutInProgress();

    void hideLogoutInProgress();

    void navigateToWelcomeScreen();

    void showLogoutButton();

    void showSupportButton();

    void showChangePasswordButton();

    void navigateToCreatedStreamDetail(String streamId);

    void showShotShared();

    void setFollowing(Boolean following);

    void showListing();

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

    void showRemovePhotoConfirmation();

    void setupAnalytics(boolean isCurrentUser);

    void showVerifiedUser();

    void hideVerifiedUser();

    void showStreamsCount();

    void setStreamsCount(Integer streams);

    void hideStreamsCount();

    void blockUser(UserModel userModel);

    void unblockUser(UserModel userModel);

    void showReportUserButton();

    void goToReportEmail(String currentUserId, String idUser);

    void showBanUserConfirmation(UserModel userModel);

    void showBlockUserButton();

    void showDefaultBlockMenu(UserModel userModel);

    void showBlockedMenu(UserModel userModel);

    void showBannedMenu(UserModel userModel);

    void showBlockAndBannedMenu(UserModel userModel);

    void showUnbanUserConfirmation(UserModel userModel);
}
