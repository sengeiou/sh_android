package com.shootr.android.ui.presenter.interactorwrapper;

import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.timeline.GetStreamHoldingTimelineInteractor;
import javax.inject.Inject;

public class StreamHoldingTimelineInteractorsWrapper {

    private final GetStreamHoldingTimelineInteractor getStreamHoldingTimelineInteractor;

    @Inject public StreamHoldingTimelineInteractorsWrapper(
      GetStreamHoldingTimelineInteractor getStreamHoldingTimelineInteractor) {
        this.getStreamHoldingTimelineInteractor = getStreamHoldingTimelineInteractor;
    }

    public void loadTimeline(String idStream, String idUser, Interactor.Callback<Timeline> callback) {
        getStreamHoldingTimelineInteractor.loadStreamHoldingTimeline(idStream, idUser, callback);
    }

}
