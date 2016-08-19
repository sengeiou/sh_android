package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.model.HighlightedShotModel;
import com.shootr.mobile.ui.views.HighlightedShotsView;

public class NullHighlightedShotsView implements HighlightedShotsView {

  @Override public void showHighlightedShot(HighlightedShotModel highlightedShots) {
    /* no-op */
  }

  @Override public void hideHighlightedShots() {
    /* no-op */
  }

  @Override
  public void refreshHighlightedShots(HighlightedShotModel highlightedShotModels) {
    /* no-op */
  }

  @Override public void showDismissDialog(String idHighlightShot) {
    /* no-op */
  }

  @Override public void updateHighlightShotInfo(HighlightedShotModel highlightedShotModel) {
    /* no-op */
  }

  @Override public void setHighlightShotBackground(Boolean isAdmin) {
    /* no-op */
  }
}
