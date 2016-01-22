package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface StreamsListView extends LoadDataView {

    void renderStream(List<StreamResultModel> streams);

    void setCurrentWatchingStreamId(StreamResultModel streamId);

    void showContent();

    void navigateToStreamTimeline(String idStream, String tag, String authorId);

    void navigateToCreatedStreamDetail(String streamId);

    void showAddedToFavorites();

    void showStreamShared();

    void showContextMenuWithMute(StreamResultModel stream);

    void showContextMenuWithUnmute(StreamResultModel stream);

    void setMutedStreamIds(List<String> mutedStreamIds);
}
