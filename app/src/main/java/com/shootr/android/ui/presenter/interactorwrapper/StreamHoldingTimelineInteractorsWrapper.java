package com.shootr.android.ui.presenter.interactorwrapper;

import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.timeline.GetOlderHoldingStreamTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.GetStreamHoldingTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.RefreshHoldingStreamTimelineInteractor;
import javax.inject.Inject;

public class StreamHoldingTimelineInteractorsWrapper {

    private final GetStreamHoldingTimelineInteractor getStreamHoldingTimelineInteractor;
    private final RefreshHoldingStreamTimelineInteractor refreshHoldingStreamTimelineInteractor;
    private final GetOlderHoldingStreamTimelineInteractor getOlderHoldingStreamTimelineInteractor;

    @Inject public StreamHoldingTimelineInteractorsWrapper(
      GetStreamHoldingTimelineInteractor getStreamHoldingTimelineInteractor,
      RefreshHoldingStreamTimelineInteractor refreshHoldingStreamTimelineInteractor,
      GetOlderHoldingStreamTimelineInteractor getOlderHoldingStreamTimelineInteractor) {
        this.getStreamHoldingTimelineInteractor = getStreamHoldingTimelineInteractor;
        this.refreshHoldingStreamTimelineInteractor = refreshHoldingStreamTimelineInteractor;
        this.getOlderHoldingStreamTimelineInteractor = getOlderHoldingStreamTimelineInteractor;
    }

    public void loadTimeline(String idStream, String idUser, Interactor.Callback<Timeline> callback) {
        getStreamHoldingTimelineInteractor.loadStreamHoldingTimeline(idStream, idUser, callback);
    }

    public void refreshTimeline(String streamId, String idUser, Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback) {
        refreshHoldingStreamTimelineInteractor.refreshHoldingStreamTimeline(streamId, idUser, callback, errorCallback);
    }

    public void obtainOlderTimeline(long currentOldestDate, String idUser, Interactor.Callback<Timeline> callback,
      Interactor.ErrorCallback errorCallback) {
        getOlderHoldingStreamTimelineInteractor.loadOlderHoldingStreamTimeline(idUser,
          currentOldestDate,
          callback,
          errorCallback);
    }

}
