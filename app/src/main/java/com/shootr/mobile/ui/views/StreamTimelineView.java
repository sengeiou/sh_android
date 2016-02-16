package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
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

    void hideHoldingShots();

    void showAllStreamShots();

    void showHoldingShots();

    void hideAllStreamShots();

    void setTitle(String shortTitle);

    Integer getFirstVisiblePosition();

    void setPosition(int newPosition);

    void showTimelineIndicator(Integer numberNewShots);

    void hideTimelineIndicator();
}
