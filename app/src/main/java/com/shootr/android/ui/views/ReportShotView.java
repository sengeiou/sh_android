package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;

public interface ReportShotView {

    void goToReport(String sessionToken, ShotModel shotModel);

    void showEmailNotConfirmedError();

    void showContextMenu(ShotModel shotModel);

    void showHolderContextMenu(ShotModel shotModel);

    void notifyDeletedShot(ShotModel shotModel);
}
