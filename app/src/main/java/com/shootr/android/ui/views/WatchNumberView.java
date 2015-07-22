package com.shootr.android.ui.views;


public interface WatchNumberView {

    void showWatchingPeopleCount(Integer peopleWatchingCount);

    void hideWatchingPeopleCount();

    void navigateToStreamDetail(String idStream);
}
