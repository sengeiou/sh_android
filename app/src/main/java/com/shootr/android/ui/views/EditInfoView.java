package com.shootr.android.ui.views;

public interface EditInfoView {

    void setSendButonEnabled(boolean enabled);

    void setTitle(String title);

    void setWatchingStatus(boolean watching);

    void closeScreen();

    void showDeleteMatchConfirmation(String confirmationTitle, String confirmationMessage);

    String getPlaceText();

    void setPlaceText(String place);

    void disablePlaceText();

    boolean getWatchingStatus();

    void enablePlaceText();

    void setFocusOnPlace();

    void alertPlaceNotWatchingNotAllow();

    void showNotificationsAlert();
}
