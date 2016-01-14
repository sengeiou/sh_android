package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.views.StreamTimelineOptionsView;

public class NullStreamTimelineOptionsView implements StreamTimelineOptionsView {

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

    @Override public void showUnmuteButton() {
        /* no-op */
    }

    @Override public void showMuteButton() {
        /* no-op */
    }

    @Override public void hideMuteButton() {
        /* no-op */
    }

    @Override public void hideUnmuteButton() {
        /* no-op */
    }
}
