package com.shootr.mobile.ui.presenter.interactorwrapper;

import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.GetActivityTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.GetOlderActivityTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.RefreshActivityTimelineInteractor;
import javax.inject.Inject;

public class ActivityTimelineInteractorsWrapper {

    private final RefreshActivityTimelineInteractor refreshActivityTimelineInteractor;
    private final GetActivityTimelineInteractor getActivityTimelineInteractor;
    private final GetOlderActivityTimelineInteractor getOlderActivityTimelineInteractor;

    @Inject
    public ActivityTimelineInteractorsWrapper(RefreshActivityTimelineInteractor refreshActivityTimelineInteractor,
      GetActivityTimelineInteractor getActivityTimelineInteractor,
      GetOlderActivityTimelineInteractor getOlderActivityTimelineInteractor) {
        this.refreshActivityTimelineInteractor = refreshActivityTimelineInteractor;
        this.getActivityTimelineInteractor = getActivityTimelineInteractor;
        this.getOlderActivityTimelineInteractor = getOlderActivityTimelineInteractor;
    }

    public void loadTimeline(Interactor.Callback<ActivityTimeline> callback) {
        getActivityTimelineInteractor.loadActivityTimeline(callback);
    }

    public void refreshTimeline(Interactor.Callback<ActivityTimeline> callback, Interactor.ErrorCallback errorCallback) {
        refreshActivityTimelineInteractor.refreshActivityTimeline(callback, errorCallback);
    }

    public void obtainOlderTimeline(long currentOldestDate, Interactor.Callback<ActivityTimeline> callback,
      Interactor.ErrorCallback errorCallback) {
        getOlderActivityTimelineInteractor.loadOlderActivityTimeline(currentOldestDate, callback, errorCallback);
    }
}
