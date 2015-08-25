package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.views.ActivityTimelineView;
import java.util.List;

public class NullActivityTimelineView implements ActivityTimelineView {

    @Override public void setActivities(List<ActivityModel> activities) {
        /* no-op */
    }

    @Override public void hideActivities() {
        /* no-op */
    }

    @Override public void showActivities() {
        /* no-op */
    }

    @Override public void addNewActivities(List<ActivityModel> newActivities) {
        /* no-op */
    }

    @Override public void addOldActivities(List<ActivityModel> oldActivities) {
        /* no-op */
    }

    @Override public void showLoadingOldActivities() {
        /* no-op */
    }

    @Override public void hideLoadingOldActivities() {
        /* no-op */
    }

    @Override public void showLoadingText() {
        /* no-op */
    }

    @Override public void hideLoadingText() {
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
