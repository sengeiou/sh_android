package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.views.WatchNumberView;

public class NullWatchNumberView implements WatchNumberView {

    @Override public void showWatchingPeopleCount(Long[] peopleWatchingCount) {
        /* no-op */
    }

    @Override public void hideWatchingPeopleCount() {
        /* no-op */
    }

    @Override public void navigateToStreamDetail(String idStream) {
        /* no-op */
    }
}
