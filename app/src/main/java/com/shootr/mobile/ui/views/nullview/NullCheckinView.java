package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.views.CheckinView;

public class NullCheckinView implements CheckinView {

    @Override public void disableCheckinButton() {
        /* no-op */
    }

    @Override public void enableCheckinButton() {
        /* no-op */
    }

    @Override public void showCheckinError() {
        /* no-op */
    }

    @Override public void showCheckinConfirmation() {
        /* no-op */
    }

    @Override public void showCheckinDone() {
        /* no-op */
    }
}
