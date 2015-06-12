package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventResultModel;
import java.util.List;

public interface ListingView {

    void renderEvents(List<EventResultModel> events);

    void showContent();

    void navigateToEventTimeline(String idEvent, String title);

    void hideLoading();

    void showLoading();
}
