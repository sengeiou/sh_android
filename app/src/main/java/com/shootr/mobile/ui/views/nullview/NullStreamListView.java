package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.views.StreamsListView;

import java.util.List;

public class NullStreamListView implements StreamsListView {

    @Override public void renderStream(List<StreamResultModel> streams) {
        /* no-op */
    }

    @Override public void setCurrentWatchingStreamId(StreamResultModel streamId) {
        /* no-op */
    }

    @Override public void showContent() {
        /* no-op */
    }

    @Override public void navigateToStreamTimeline(String idStream, String tag, String authorId) {
        /* no-op */
    }

    @Override public void navigateToCreatedStreamDetail(String streamId) {
        /* no-op */
    }

    @Override
    public void showAddedToFavorites() {
        /* no-op */
    }

    @Override public void showStreamShared() {
        /* no-op */
    }

    @Override public void showContextMenuWithMute(StreamResultModel stream) {
        /* no-op */
    }

    @Override public void showContextMenuWithUnmute(StreamResultModel stream) {
        /* no-op */
    }

    @Override public void setMutedStreamIds(List<String> mutedStreamIds) {
        /* no-op */
    }

    @Override public void scrollListToTop() {
        /* no-op */
    }

    @Override public void showEmpty() {
        /* no-op */
    }

    @Override public void hideEmpty() {
        /* no-op */
    }

    @Override public void showLoading() {
        /* no-op */
    }

    @Override public void hideLoading() {
        /* no-op */
    }

    @Override public void showError(String message) {
        /* no-op */
    }
}
