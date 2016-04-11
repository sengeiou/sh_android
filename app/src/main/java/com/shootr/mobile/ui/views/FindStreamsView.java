package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamResultModel;
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

    void navigateToStreamTimeline(String idStream, String streamShortTitle, String authorId);

    void showAddedToFavorites();

    void showStreamShared();
}
