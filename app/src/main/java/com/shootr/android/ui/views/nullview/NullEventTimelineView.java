package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.EventTimelineView;

import java.util.List;

public class NullEventTimelineView implements EventTimelineView {

    @Override public void setShots(List<ShotModel> shots) {
        /* no-op */
    }

    @Override public void hideShots() {
        /* no-op */
    }

    @Override public void showShots() {
        /* no-op */
    }

    @Override public void addNewShots(List<ShotModel> newShots) {
        /* no-op */
    }

    @Override public void addOldShots(List<ShotModel> oldShots) {
        /* no-op */
    }

    @Override public void showLoadingOldShots() {
        /* no-op */
    }

    @Override public void hideLoadingOldShots() {
        /* no-op */
    }

    @Override public void showEmpty() {
        /* no-op */
    }

    @Override public void hideEmpty() {
        /* no-op */
    }

    @Override public void showLoading() {
        /* no-op */
    }

    @Override public void hideLoading() {
        /* no-op */
    }

    @Override public void showError(String message) {
        /* no-op */
    }
}
