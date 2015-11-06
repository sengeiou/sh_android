package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;
import java.util.List;

public interface AllShotsView {

    void showError(String messageForError);

    void hideLoading();

    void setShots(List<ShotModel> shotModels);

    void hideEmpty();

    void showShots();

    void showEmpty();

    void hideShots();

    void showLoading();

    void showLoadingOldShots();

    void hideLoadingOldShots();

    void addOldShots(List<ShotModel> shotModels);

    void showShotShared();
}
