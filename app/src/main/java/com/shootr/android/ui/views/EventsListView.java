package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface EventsListView extends LoadDataView {

    void renderEvents(List<StreamResultModel> events);

    void setCurrentWatchingEventId(StreamResultModel eventId);

    void showContent();

    void navigateToEventTimeline(String idEvent, String title);

    void showNotificationsOff();
}
