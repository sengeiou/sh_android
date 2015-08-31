package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface ListingView extends LoadDataView {

    void renderStreams(List<StreamResultModel> streams);

    void showContent();

    void navigateToStreamTimeline(String idStream, String title);


    void setFavoriteStreams(List<StreamResultModel> transform);

    void hideContent();

    void navigateToCreatedStreamDetail(String streamId);

    void showStreamShared();
}
