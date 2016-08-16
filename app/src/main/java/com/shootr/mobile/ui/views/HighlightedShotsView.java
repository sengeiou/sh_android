package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.HighlightedShotModel;

public interface HighlightedShotsView {
  void showHighlightedShot(HighlightedShotModel highlightedShots);

  void hideHighlightedShots();

  void refreshHighlightedShots(HighlightedShotModel highlightedShotModels);

  void showDismissDialog(String idHighlightShot);

  void updateHighlightShotInfo(HighlightedShotModel highlightedShotModel);

  void setHighlightShotBackground(Boolean isAdmin);
}
