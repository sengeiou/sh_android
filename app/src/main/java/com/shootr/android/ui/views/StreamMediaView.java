package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;
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
