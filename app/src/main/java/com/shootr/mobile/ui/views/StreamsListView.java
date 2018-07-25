package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.LandingStreamsModel;
import com.shootr.mobile.ui.model.PromotedLandingItemModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.ArrayList;
import java.util.List;

public interface StreamsListView extends LoadDataView {

    void renderStream(List<StreamResultModel> streams);

    void setCurrentWatchingStreamId(StreamResultModel streamId);

    void showContent();

    void navigateToStreamTimeline(String idStream, String tag, String authorId);

    void navigateToCreatedStreamDetail(String streamId);

    void showAddedToFavorites();

    void showRemovedFromFavorites();

    void showStreamShared();

    void showContextMenuWithMute(StreamModel stream);

    void showContextMenuWithUnmute(StreamModel stream);

    void scrollListToTop();

    void updateChannelBadge(int unreadChannels, boolean followingsChannel);

    void navigateToMyStreams(String currentUserId, boolean isCurrentUser);

    void renderLanding(LandingStreamsModel landingStreamsModel);

    void renderFollow(StreamModel streamModel);

    void renderMute(StreamModel stream);

  void renderPromoteds(ArrayList<PromotedLandingItemModel> promotedItems);
}
