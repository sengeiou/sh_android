package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface FavoritesListView extends LoadDataView {

    void renderFavorites(List<StreamResultModel> streamModels);

    void showContent();

    void hideContent();

    void navigateToStreamTimeline(String idStream, String title, String authorId);

    void showStreamShared();
}
