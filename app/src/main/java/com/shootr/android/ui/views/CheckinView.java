package com.shootr.android.ui.views;

public interface CheckinView {

    void showCheckinButton();

    void hideCheckinButton();

    void disableCheckinButton();

    void enableCheckinButton();

    void showCheckinError();

    void showCheckinNotification(Boolean userWantsNotification);

    void showCheckedInNotification();
}
