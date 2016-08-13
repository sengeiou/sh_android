package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;

public interface ReportShotView {

    void handleReport(String sessionToken, ShotModel shotModel);

    void showEmailNotConfirmedError();

    void notifyDeletedShot(ShotModel shotModel);

    void showError(String errorMessage);

    void showBlockFollowingUserAlert();

    void showUserBlocked();

    void showUserUnblocked();

    void showBlockUserConfirmation();

    void showErrorLong(String messageForError);

    void showUserBanned();

    void showUserUnbanned();

    void goToReport(String sessionToken, ShotModel shotModel);

    void showAlertLanguageSupportDialog(String sessionToken, ShotModel shotModel);

    void showHolderContextMenu(ShotModel shot);

    void showContextMenu(ShotModel shotModel);

    void showAuthorContextMenuWithPin(ShotModel shotModel);

    void showContextMenuWithUnblock(ShotModel shotModel);

    void showAuthorContextMenuWithoutPin(ShotModel shotModel);

    void showAuthorContextMenuWithPinAndHighlight(ShotModel shot);

    void showAuthorContextMenuWithoutPinAndHighlight(ShotModel shot);

    void showContributorContextMenuWithPinAndHighlight(ShotModel shot);

    void showContributorContextMenuWithoutPinAndHighlight(ShotModel shot);

    void showContributorContextMenu(ShotModel shot);
}
