package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface FavoritesListView extends LoadDataView {

    void showFavorites(List<EventResultModel> eventModels);
}
