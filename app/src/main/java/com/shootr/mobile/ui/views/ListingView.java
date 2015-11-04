package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface ListingView extends LoadDataView {

    void renderHoldingStreams(List<StreamResultModel> streams);

    void renderFavoritedStreams(List<StreamResultModel> listingUsrFavoritedStreams);

    void showContent();

    void navigateToStreamTimeline(String idStream, String tag, String authorId);

    void setCurrentUserFavorites(List<StreamResultModel> transform);

    void hideContent();

    void navigateToCreatedStreamDetail(String streamId);

    void showStreamShared();

    void hideSectionTitles();

    void showSectionTitles();

    void showCurrentUserContextMenu(StreamResultModel stream);

    void showContextMenu(StreamResultModel stream);

    void askRemoveStreamConfirmation();

    void updateStreams();

    void showAddStream();

    void hideAddStream();
}
