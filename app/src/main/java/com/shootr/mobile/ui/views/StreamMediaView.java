package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;

import java.util.List;

public interface StreamMediaView {

    void setMedia(List<ShotModel> shotsWithMedia);

    void hideEmpty();

    void showEmpty();

    void showLoading();

    void hideLoading();

    void showError(String errorMessage);

    void addOldMedia(List<ShotModel> shotModels);

    void showLoadingOldMedia();

    void hideLoadingOldMedia();

    void showNoMoreMedia();
}
