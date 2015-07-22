package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface FavoritesListView extends LoadDataView {

    void renderFavorites(List<StreamResultModel> eventModels);

    void showContent();

    void hideContent();

    void navigateToEventTimeline(String idEvent, String title);
}
