package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.DiscoverTimelineModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.views.base.LoadDataView;

public interface DiscoverTimelineView extends LoadDataView {

  void renderDiscover(DiscoverTimelineModel discoverTimelineModel);

  void scrollListToTop();

  void navigateToStreamTimeline(String idStream);

  void navigateToShotDetail(ShotModel shotModel);

  void navigateToUserProfile(String userId);

  void showError(String message);

  void showEmpty();

  void renderNewFavorite(StreamModel streamModel);

  void removeFavorite(StreamModel streamModel);

  void renderNiceMarked(ShotModel shotModel);

  void renderNiceUnmarked(String idShot);

  void showReshot(ShotModel shotModel, boolean mark);
}
