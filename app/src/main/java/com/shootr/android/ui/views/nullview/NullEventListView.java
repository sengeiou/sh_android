package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.EventsListView;
import java.util.List;

public class NullEventListView implements EventsListView {

    @Override public void renderEvents(List<EventResultModel> events) {
        /* no-op */
    }

    @Override public void setCurrentCheckedInEventId(String eventId) {
        /* no-op */
    }

    @Override public void setCurrentWatchingEventId(EventResultModel eventId) {
        /* no-op */
    }

    @Override public void showContent() {
        /* no-op */
    }

    @Override public void navigateToEventTimeline(String idEvent, String title) {
        /* no-op */
    }

    @Override public void showNotificationsOff() {
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
