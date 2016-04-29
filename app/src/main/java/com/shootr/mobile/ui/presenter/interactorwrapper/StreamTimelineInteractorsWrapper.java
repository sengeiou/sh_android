package com.shootr.mobile.ui.presenter.interactorwrapper;

import com.shootr.mobile.domain.Timeline;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.GetOlderStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.GetStreamTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.RefreshStreamTimelineInteractor;
import javax.inject.Inject;

public class StreamTimelineInteractorsWrapper {

    private final RefreshStreamTimelineInteractor refreshStreamTimelineInteractor;
    private final GetStreamTimelineInteractor getStreamTimelineInteractor;
    private final GetOlderStreamTimelineInteractor getOlderStreamTimelineInteractor;

    @Inject public StreamTimelineInteractorsWrapper(RefreshStreamTimelineInteractor refreshStreamTimelineInteractor,
      GetStreamTimelineInteractor getStreamTimelineInteractor,
      GetOlderStreamTimelineInteractor getOlderStreamTimelineInteractor) {
        this.refreshStreamTimelineInteractor = refreshStreamTimelineInteractor;
        this.getStreamTimelineInteractor = getStreamTimelineInteractor;
        this.getOlderStreamTimelineInteractor = getOlderStreamTimelineInteractor;
    }

    public void loadTimeline(String idStream, Boolean hasBeenPaused, Interactor.Callback<Timeline> callback) {
        getStreamTimelineInteractor.loadStreamTimeline(idStream, hasBeenPaused, callback);
    }

    public void refreshTimeline(String streamId, Long lastRefreshDate, Boolean hasBeenPaused,
      Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback) {
        refreshStreamTimelineInteractor.refreshStreamTimeline(streamId,
          lastRefreshDate,
          hasBeenPaused,
          callback,
          errorCallback);
    }

    public void obtainOlderTimeline(Long currentOldestDate, Interactor.Callback<Timeline> callback,
      Interactor.ErrorCallback errorCallback) {
        getOlderStreamTimelineInteractor.loadOlderStreamTimeline(currentOldestDate, callback, errorCallback);
    }
}
