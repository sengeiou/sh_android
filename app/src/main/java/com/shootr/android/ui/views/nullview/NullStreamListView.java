package com.shootr.android.ui.views.nullview;

import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.views.StreamsListView;
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

    @Override public void navigateToStreamTimeline(String idStream, String tag) {
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
