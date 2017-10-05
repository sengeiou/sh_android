package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;
import java.util.List;

public interface StreamTimelineView extends TimelineView  {

    void setShots(List<ShotModel> shotModels);

    void addAbove(List<ShotModel> shotModels);

    void updateShotsInfo(List<ShotModel> shots);

    void addShots(List<ShotModel> shotModels);

    void addOldShots(List<ShotModel> oldShots);

    void hideHoldingShots();

    void showAllStreamShots();

    void showHoldingShots();

    void hideAllStreamShots();

    void showPinnedMessage(String topic);

    void hidePinnedMessage();

    void showPinMessageNotification(String message);

    void hideStreamViewOnlyIndicator();

    void showStreamViewOnlyIndicator();

    void showChecked();

    void openCtaAction(String link);

    void storeCtaClickLink(ShotModel shotModel);

    void showFilterAlert();

    void renderNice(ShotModel shotModel);

    void renderUnnice(String idShot);

    void setupCheckInShowcase();

    void setReshoot(String idShot, boolean mark);

    void showEmpty(boolean isFilterActivated);
}
