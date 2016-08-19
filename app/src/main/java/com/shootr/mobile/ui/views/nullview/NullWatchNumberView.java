package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.views.WatchNumberView;

public class NullWatchNumberView implements WatchNumberView {

    @Override public void showWatchingPeopleCount(Integer[] peopleWatchingCount) {
        /* no-op */
    }

    @Override public void hideWatchingPeopleCount() {
        /* no-op */
    }

    @Override public void navigateToStreamDetail(String idStream) {
        /* no-op */
    }

    @Override public void showParticipantsCount(Integer[] peopleWatchingCount) {
        /* no-op */
    }
}
