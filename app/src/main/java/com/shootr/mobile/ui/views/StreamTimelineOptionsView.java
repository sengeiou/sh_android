package com.shootr.mobile.ui.views;

public interface StreamTimelineOptionsView {

    void showAddToFavoritesButton();

    void hideAddToFavoritesButton();

    void showRemoveFromFavoritesButton();

    void hideRemoveFromFavoritesButton();

    void showAddedToFavorites();

    void showError(String errorMessage);

    void showUnmuteButton();

    void showMuteButton();

    void hideMuteButton();

    void hideUnmuteButton();
}
