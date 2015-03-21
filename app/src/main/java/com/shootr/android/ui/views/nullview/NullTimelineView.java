package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.TimelineView;

import java.util.List;

public class NullTimelineView implements TimelineView {

    @Override public void setShots(List<ShotModel> shots) {
        /* no-op */
    }

    @Override public void hideShots() {
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
