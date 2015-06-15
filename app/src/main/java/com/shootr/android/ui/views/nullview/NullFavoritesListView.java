package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.model.EventResultModel;
import com.shootr.android.ui.views.FavoritesListView;
import java.util.List;

public class NullFavoritesListView implements FavoritesListView {

    @Override
    public void showFavorites(List<EventResultModel> eventModels) {
        /* no-op */
    }

    @Override
    public void showEmpty() {
        /* no-op */
    }

    @Override
    public void hideEmpty() {
        /* no-op */
    }

    @Override
    public void showLoading() {
        /* no-op */
    }

    @Override
    public void hideLoading() {
        /* no-op */
    }

    @Override
    public void showError(String message) {
        /* no-op */
    }
}
