package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.views.base.LoadDataView;
import java.util.List;

public interface DiscoverView extends LoadDataView {

  void renderDiscover(List<DiscoveredModel> discoveredModels);

  void scrollListToTop();

  void navigateToStreamTimeline(String idStream);

  void navigateToShotDetail(ShotModel shotModel);

  void navigateToUserProfile(String userId);

  void showError(String message);
}
