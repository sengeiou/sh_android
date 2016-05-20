package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;

public interface ReportShotView {

    void handleReport(String sessionToken, ShotModel shotModel);

    void showEmailNotConfirmedError();

    void showContextMenu(ShotModel shotModel);

    void showAuthorContextMenuWithPin(ShotModel shotModel);

    void notifyDeletedShot(ShotModel shotModel);

    void showError(String errorMessage);

    void showContextMenuWithUnblock(ShotModel shotModel);

    void showBlockFollowingUserAlert();

    void showUserBlocked();

    void showUserUnblocked();

    void showBlockUserConfirmation();

    void showErrorLong(String messageForError);

    void showUserBanned();

    void showUserUnbanned();

    void showAuthorContextMenuWithoutPin(ShotModel shotModel);

    void goToReport(String sessionToken, ShotModel shotModel);

    void showAlertLanguageSupportDialog(String sessionToken, ShotModel shotModel);

    void showHolderContextMenu(ShotModel shot);
}
