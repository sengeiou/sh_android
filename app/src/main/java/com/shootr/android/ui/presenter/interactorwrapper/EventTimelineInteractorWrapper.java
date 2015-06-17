package com.shootr.android.ui.presenter.interactorwrapper;

import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.timeline.GetEventTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.GetOlderEventTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.RefreshEventTimelineInteractor;
import javax.inject.Inject;

public class EventTimelineInteractorWrapper implements TimelineInteractorWrapper {

    private final RefreshEventTimelineInteractor refreshEventTimelineInteractor;
    private final GetEventTimelineInteractor getEventTimelineInteractor;
    private final GetOlderEventTimelineInteractor getOlderEventTimelineInteractor;

    @Inject public EventTimelineInteractorWrapper(RefreshEventTimelineInteractor refreshEventTimelineInteractor,
      GetEventTimelineInteractor getEventTimelineInteractor,
      GetOlderEventTimelineInteractor getOlderEventTimelineInteractor) {
        this.refreshEventTimelineInteractor = refreshEventTimelineInteractor;
        this.getEventTimelineInteractor = getEventTimelineInteractor;
        this.getOlderEventTimelineInteractor = getOlderEventTimelineInteractor;
    }

    @Override public void loadTimeline(Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback) {
        getEventTimelineInteractor.loadEventTimeline(callback, errorCallback);
    }

    @Override
    public void refreshTimeline(Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback) {
        refreshEventTimelineInteractor.refreshEventTimeline(callback, errorCallback);
    }

    @Override public void obtainOlderTimeline(long currentOldestDate, Interactor.Callback<Timeline> callback,
      Interactor.ErrorCallback errorCallback) {
        getOlderEventTimelineInteractor.loadOlderEventTimeline(currentOldestDate, callback, errorCallback);
    }
}
