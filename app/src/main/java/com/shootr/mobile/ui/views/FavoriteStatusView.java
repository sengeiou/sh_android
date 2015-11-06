package com.shootr.mobile.ui.views;

public interface FavoriteStatusView {

    void showAddToFavoritesButton();

    void hideAddToFavoritesButton();

    void showRemoveFromFavoritesButton();

    void hideRemoveFromFavoritesButton();

    void showAddedToFavorites();

    void showError(String errorMessage);
}
