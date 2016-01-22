package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.views.FavoritesListView;
import java.util.List;

public class NullFavoritesListView implements FavoritesListView {

    @Override
    public void renderFavorites(List<StreamResultModel> streamModels) {
        /* no-op */
    }

    @Override
    public void showContent() {
        /* no-op */
    }

    @Override
    public void hideContent() {
        /* no-op */
    }

    @Override
    public void navigateToStreamTimeline(String idStream, String title, String authorId) {
        /* no-op */
    }

    @Override
    public void showStreamShared() {
        /* no-op */
    }

    @Override public void setMutedStreamIds(List<String> mutedStreamIds) {
        /* no-op */
    }

    @Override public void showContextMenuWithUnmute(StreamResultModel stream) {
        /* no-op */
    }

    @Override public void showContextMenuWithMute(StreamResultModel stream) {
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
