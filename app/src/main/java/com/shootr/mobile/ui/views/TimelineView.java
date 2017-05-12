package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.views.base.LoadDataView;

/**
 * Created by miniserver on 24/11/16.
 */

public interface TimelineView extends LoadDataView {

  void hideShots();

  void showShots();

  void showLoadingOldShots();

  void hideLoadingOldShots();

  void showCheckingForShots();

  void hideCheckingForShots();

  void setTitle(String title);

  void sendAnalythicsEnterTimeline();

  void setImage(String avatarImage, String username);

  void showNewShotsIndicator(Integer numberNewShots);

  void hideNewShotsIndicator();

  void setRemainingCharactersCount(int remainingCharacters);

  void setRemainingCharactersColorValid();

  void setRemainingCharactersColorInvalid();

}
