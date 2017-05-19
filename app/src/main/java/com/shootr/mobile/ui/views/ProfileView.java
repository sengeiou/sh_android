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

    void notifyReshoot(String idShot, boolean mark);

    void setFollowing(Boolean following);

    void setUserInfo(UserModel userModel);

    void showAllShotsButton();

    void hideAllShotsButton();

    void showError(String messageForError);

    void showEditProfileButton();

    void showFollowButton();

    void showUnfollowButton();

    void showAddPhoto();

    void openPhoto(String photo);

    void openEditPhotoMenu(boolean showRemove, String photo);

    void goToWebsite(String website);

    void goToFollowersList(String idUser);

    void goToFollowingList(String idUser);

    void renderLastShots(List<ShotModel> shots);

    void showUnfollowConfirmation(String username);

    void goToAllShots(String idUser);

    void showUserSettings();

    void showLatestShots();

    void hideLatestShots();

    void showReshotsHeader();

    void showLoadingPhoto();

    void hideLoadingPhoto();

    void showLoading();

    void hideLoading();

    void showRemovePhotoConfirmation();

    void setupAnalytics(boolean isCurrentUser);

    void showVerifiedUser();

    void hideVerifiedUser();

    void showStreamsCount();

    void setStreamsCount(Integer streams);

    void hideChannelButton();

    void blockUser(UserModel userModel);

    void unblockUser(UserModel userModel);

    void showReportUserButton();

    void showUnblockUserButton();

    void goToReportEmail(String currentUserId, String idUser);

    void showBlockUserButton();

    void showBlockedMenu(UserModel userModel);

    void resetTimelineAdapter();

    void goToChannelsList();

    void goToChannelTimeline(String idTargetUser);

    void hideEditMenu();

    void showEditMenu();
}
