package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import java.util.List;
import java.util.Locale;

public interface ListingView {

    void renderEvents(List<EventModel> events);

    void showContent();

    void navigateToEventTimeline(String idEvent, String title);

    void hideLoading();

    void showLoading();

    Locale getLocale();
}
