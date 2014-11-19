package com.shootr.android.ui.views;

public interface EditInfoView {

    void setSendButonEnabled(boolean enabled);

    void setTitle(String title);

    void setWatchingStatus(boolean watching);

    void closeScreen();

    void showDeleteMatchConfirmation(String matchTitle);
}
