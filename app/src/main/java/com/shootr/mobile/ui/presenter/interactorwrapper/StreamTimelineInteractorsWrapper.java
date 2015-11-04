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

    public void loadTimeline(String idStream, Interactor.Callback<Timeline> callback) {
        getStreamTimelineInteractor.loadStreamTimeline(idStream, callback);
    }

    public void refreshTimeline(String streamId, Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback) {
        refreshStreamTimelineInteractor.refreshStreamTimeline(streamId, callback, errorCallback);
    }

    public void obtainOlderTimeline(long currentOldestDate, Interactor.Callback<Timeline> callback,
      Interactor.ErrorCallback errorCallback) {
        getOlderStreamTimelineInteractor.loadOlderStreamTimeline(currentOldestDate, callback, errorCallback);
    }
}