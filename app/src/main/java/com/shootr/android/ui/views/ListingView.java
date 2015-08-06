package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import java.util.List;

public interface ListingView {

    void renderStreams(List<StreamResultModel> streams);

    void showContent();

    void navigateToStreamTimeline(String idStream, String title);

    void hideLoading();

    void showLoading();

    void showError(String errorMessage);

    void setFavoriteStreams(List<StreamResultModel> transform, List<StreamResultModel> listing);
}
