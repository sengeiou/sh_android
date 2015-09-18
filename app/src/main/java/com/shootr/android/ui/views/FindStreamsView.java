package com.shootr.android.ui.views;

import com.shootr.android.ui.model.StreamResultModel;
import java.util.List;

public interface FindStreamsView {

    void hideContent();

    void hideKeyboard();

    void showLoading();

    void hideLoading();

    void showEmpty();

    void showContent();

    void hideEmpty();

    void renderStreams(List<StreamResultModel> streamModels);

    void showError(String errorMessage);

    void navigateToStreamTimeline(String idStream, String streamTitle);

    void showAddedToFavorites();

    void showStreamShared();
}
