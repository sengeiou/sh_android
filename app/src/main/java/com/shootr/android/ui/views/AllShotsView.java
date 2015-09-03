package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;
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
