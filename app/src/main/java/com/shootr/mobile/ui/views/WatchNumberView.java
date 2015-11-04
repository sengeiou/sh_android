package com.shootr.mobile.ui.views;


public interface WatchNumberView {

    void showWatchingPeopleCount(Integer peopleWatchingCount);

    void hideWatchingPeopleCount();

    void navigateToStreamDetail(String idStream);
}
