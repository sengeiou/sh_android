package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.views.FavoriteStatusView;

public class NullFavoriteStatusView implements FavoriteStatusView {

    @Override
    public void showAddToFavoritesButton() {
        /* no-op */
    }

    @Override
    public void hideAddToFavoritesButton() {
        /* no-op */
    }

    @Override
    public void showRemoveFromFavoritesButton() {
        /* no-op */
    }

    @Override
    public void hideRemoveFromFavoritesButton() {
        /* no-op */
    }

    @Override
    public void showAddedToFavorites() {
        /* no-op */
    }

    @Override public void showError(String errorMessage) {
        /* no-op */
    }
}
