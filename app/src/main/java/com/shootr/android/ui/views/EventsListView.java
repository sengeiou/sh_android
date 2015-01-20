package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventResultModel;
import java.util.List;

public interface EventsListView {

    void renderEvents(List<EventResultModel> events);
}
