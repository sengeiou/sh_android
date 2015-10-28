package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface FavoritesListView extends LoadDataView {

    void renderFavorites(List<StreamResultModel> streamModels);

    void showContent();

    void hideContent();

    void navigateToStreamTimeline(String idStream, String title, String authorId);

    void showStreamShared();
}
