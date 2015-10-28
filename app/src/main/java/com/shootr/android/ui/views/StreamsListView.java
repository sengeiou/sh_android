package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface StreamsListView extends LoadDataView {

    void renderStream(List<StreamResultModel> streams);

    void setCurrentWatchingStreamId(StreamResultModel streamId);

    void showContent();

    void navigateToStreamTimeline(String idStream, String tag, String authorId);

    void navigateToCreatedStreamDetail(String streamId);

    void showAddedToFavorites();

    void showStreamShared();
}
