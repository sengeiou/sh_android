package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface StreamTimelineView extends LoadDataView {

    void setShots(List<ShotModel> shots);

    void hideShots();

    void showShots();

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

    void setTitle(String title);

    void showNewShotsIndicator(Integer numberNewShots);

    void hideNewShotsIndicator();

    void showPinnedMessage(String topic);

    void hidePinnedMessage();

    void setRemainingCharactersCount(int remainingCharacters);

    void setRemainingCharactersColorValid();

    void setRemainingCharactersColorInvalid();

    void showPinMessageNotification(String message);

    void addAbove(List<ShotModel> shotModels);

    void updateShotsInfo(List<ShotModel> shots);

    void hideStreamViewOnlyIndicator();

    void showStreamViewOnlyIndicator();

    void addShots(List<ShotModel> shotModels);

    void showChecked();

    void openCtaAction(String link);

    void storeCtaClickLink(ShotModel shotModel);
}
