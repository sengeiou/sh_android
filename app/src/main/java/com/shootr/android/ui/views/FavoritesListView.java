package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface FavoritesListView extends LoadDataView {

    void renderFavorites(List<EventResultModel> eventModels);

    void showContent();

    void hideContent();

    void navigateToEventTimeline(String idEvent, String title);
}
