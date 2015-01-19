package com.shootr.android.ui.views;

public interface EditInfoView {

    void setSendButonEnabled(boolean enabled);

    void setTitle(String title);

    void setWatchingStatus(boolean watching);

    void closeScreen();

    String getPlaceText();

    void setPlaceText(String place);

    void disablePlaceText();

    boolean getWatchingStatus();

    void enablePlaceText();

    void setFocusOnPlace();

    void alertPlaceNotWatchingNotAllow();

    void showNotificationsAlert();
}
