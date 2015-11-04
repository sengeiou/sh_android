package com.shootr.mobile.ui.views;

public interface CheckinView {

    void disableCheckinButton();

    void enableCheckinButton();

    void showCheckinError();

    void showCheckinConfirmation();

    void showCheckinDone();
}
