package com.shootr.mobile.ui.presenter.interactorwrapper;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.GetOlderStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.GetOlderViewOnlyStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.GetStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.GetViewOnlyStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.RefreshStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.RefreshViewOnlyStreamTimelineInteractor;
import com.shootr.mobile.domain.model.stream.Timeline;
import javax.inject.Inject;

public class StreamTimelineInteractorsWrapper {

  private final RefreshStreamTimelineInteractor refreshStreamTimelineInteractor;
  private final RefreshViewOnlyStreamTimelineInteractor refreshViewOnlyStreamTimelineInteractor;
  private final GetStreamTimelineInteractor getStreamTimelineInteractor;
  private final GetViewOnlyStreamTimelineInteractor getViewOnlyStreamTimelineInteractor;
  private final GetOlderStreamTimelineInteractor getOlderStreamTimelineInteractor;
  private final GetOlderViewOnlyStreamTimelineInteractor getOlderViewOnlyStreamTimelineInteractor;

  @Inject public StreamTimelineInteractorsWrapper(
      RefreshStreamTimelineInteractor refreshStreamTimelineInteractor,
      RefreshViewOnlyStreamTimelineInteractor refreshViewOnlyStreamTimelineInteractor,
      GetStreamTimelineInteractor getStreamTimelineInteractor,
      GetViewOnlyStreamTimelineInteractor getViewOnlyStreamTimelineInteractor,
      GetOlderStreamTimelineInteractor getOlderStreamTimelineInteractor,
      GetOlderViewOnlyStreamTimelineInteractor getOlderViewOnlyStreamTimelineInteractor) {
    this.refreshStreamTimelineInteractor = refreshStreamTimelineInteractor;
    this.refreshViewOnlyStreamTimelineInteractor = refreshViewOnlyStreamTimelineInteractor;
    this.getStreamTimelineInteractor = getStreamTimelineInteractor;
    this.getViewOnlyStreamTimelineInteractor = getViewOnlyStreamTimelineInteractor;
    this.getOlderStreamTimelineInteractor = getOlderStreamTimelineInteractor;
    this.getOlderViewOnlyStreamTimelineInteractor = getOlderViewOnlyStreamTimelineInteractor;
  }

  public void loadTimeline(String idStream, boolean filterActivated, Boolean hasBeenPaused, Integer streamMode,
      Interactor.Callback<Timeline> callback) {
    if (streamMode == 0) {
      getStreamTimelineInteractor.loadStreamTimeline(idStream, filterActivated, hasBeenPaused, callback);
    } else {
      getViewOnlyStreamTimelineInteractor.loadStreamTimeline(idStream, hasBeenPaused, callback);
    }
  }

  public void refreshTimeline(String streamId, boolean filterActivated, Long lastRefreshDate, Boolean hasBeenPaused,
      Integer streamMode, Interactor.Callback<Timeline> callback,
      Interactor.ErrorCallback errorCallback) {
    if (streamMode == 0) {
      refreshStreamTimelineInteractor.refreshStreamTimeline(streamId, filterActivated, lastRefreshDate,
          hasBeenPaused, callback, errorCallback);
    } else {
      refreshViewOnlyStreamTimelineInteractor.refreshStreamTimeline(streamId, lastRefreshDate,
          hasBeenPaused, callback, errorCallback);
    }
  }

  public void obtainOlderTimeline(String streamId, boolean filterActivated, Long currentOldestDate, Integer streamMode,
      Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback) {
    if (streamMode == 0) {
      getOlderStreamTimelineInteractor.loadOlderStreamTimeline(streamId, filterActivated, currentOldestDate, callback,
          errorCallback);
    } else {
      getOlderViewOnlyStreamTimelineInteractor.loadOlderStreamTimeline(streamId, currentOldestDate, callback,
          errorCallback);
    }
  }
}
