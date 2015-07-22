package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import java.util.List;

public interface FindEventsView {

    void hideContent();

    void hideKeyboard();

    void showLoading();

    void hideLoading();

    void showEmpty();

    void showContent();

    void hideEmpty();

    void renderEvents(List<StreamResultModel> eventModels);

    void showError(String errorMessage);

    void navigateToEventTimeline(String idEvent, String eventTitle);
}
