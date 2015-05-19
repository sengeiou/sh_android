package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;
import java.util.Locale;

public interface EventsListView extends LoadDataView {

    void renderEvents(List<EventResultModel> events);

    void setCurrentVisibleCheckedInEventId(String eventId);

    void setCurrentVisibleWatchingEventId(String eventId);

    void showContent();

    void hideContent();

    void hideKeyboard();

    void navigateToEventTimeline(String idEvent, String title);
}
