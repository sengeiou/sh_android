package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface EventsListView extends LoadDataView {

    void renderEvents(List<EventResultModel> events);

    void setCurrentVisibleEventId(Long eventId);

    void showContent();

    void hideContent();

    void closeScrenWithEventResult(Long idEvent);

    void hideKeyboard();
}
