package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface EventsListView extends LoadDataView {

    void renderEvents(List<EventResultModel> events);

    void setCurrentVisibleEventId(String eventId);

    void showContent();

    void hideContent();

    void closeScrenWithEventResult(String idEvent);

    void hideKeyboard();
}
