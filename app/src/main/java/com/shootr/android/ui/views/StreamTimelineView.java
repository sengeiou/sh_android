package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface StreamTimelineView extends LoadDataView{

    void setShots(List<ShotModel> shots);

    void hideShots();

    void showShots();

    void addNewShots(List<ShotModel> newShots);

    void addOldShots(List<ShotModel> oldShots);

    void showLoadingOldShots();

    void hideLoadingOldShots();

    void showCheckingForShots();

    void hideCheckingForShots();

    void showShotShared();
}
