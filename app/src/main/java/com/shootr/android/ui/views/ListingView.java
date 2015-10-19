package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface ListingView extends LoadDataView {

    void renderHoldingStreams(List<StreamResultModel> streams);

    void renderFavoritedStreams(List<StreamResultModel> listingUsrFavoritedStreams);

    void showContent();

    void navigateToStreamTimeline(String idStream, String tag);

    void setCurrentUserFavorites(List<StreamResultModel> transform);

    void hideContent();

    void navigateToCreatedStreamDetail(String streamId);

    void showStreamShared();

    void hideSectionTitles();

    void showSectionTitles();

    void showCurrentUserContextMenu(StreamResultModel stream);

    void showContextMenu(StreamResultModel stream);

    void askRemoveStreamConfirmation();
}
