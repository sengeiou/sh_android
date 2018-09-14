package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.model.LandingStreamsModel;
import com.shootr.mobile.ui.model.PromotedLandingItemModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.views.StreamsListView;
import java.util.ArrayList;
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

    @Override public void showAddedToFavorites() {
        /* no-op */
    }

    @Override public void showRemovedFromFavorites() {
        /* no-op */
    }

    @Override public void showStreamShared() {
        /* no-op */
    }

    @Override public void showContextMenuWithMute(StreamModel stream) {
        /* no-op */
    }

    @Override public void showContextMenuWithUnmute(StreamModel stream) {
        /* no-op */
    }

    @Override public void scrollListToTop() {
        /* no-op */
    }

    @Override public void updateChannelBadge(int unreadChannels, boolean isFollowingChannels) {
        /* no-op */
    }

    @Override public void navigateToMyStreams(String currentUserId, boolean isCurrentUser) {
        /* no-op */
    }

    @Override public void renderLanding(LandingStreamsModel landingStreamsModel) {
        /* no-op */
    }

    @Override public void renderFollow(StreamModel streamModel) {
        /* no-op */
    }

    @Override public void renderMute(StreamModel stream) {
        /* no-op */
    }

    @Override public void renderPromoteds(ArrayList<PromotedLandingItemModel> promotedItems) {
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
