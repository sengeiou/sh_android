package com.shootr.mobile.ui.views;

public interface WatchNumberView {

    void showWatchingPeopleCount(Long[] peopleWatchingCount);

    void hideWatchingPeopleCount();

    void navigateToStreamDetail(String idStream);
}
