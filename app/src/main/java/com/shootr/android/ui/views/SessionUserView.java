package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;

public interface SessionUserView {

    void goToReport(String sessionToken, ShotModel shotModel);

    void showConfirmationMessage();
}
