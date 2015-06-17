package com.shootr.android.ui.presenter.interactorwrapper;

import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.timeline.GetActivityTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.GetOlderActivityTimelineInteractor;
import com.shootr.android.domain.interactor.timeline.RefreshActivityTimelineInteractor;
import javax.inject.Inject;

public class ActivityTimelineInteractorsWrapper implements ActivityTimelineInteractorWrapper {

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

    @Override public void loadTimeline(Interactor.Callback<ActivityTimeline> callback, Interactor.ErrorCallback errorCallback) {
        getActivityTimelineInteractor.loadActivityTimeline(callback, errorCallback);
    }

    @Override
    public void refreshTimeline(Interactor.Callback<ActivityTimeline> callback, Interactor.ErrorCallback errorCallback) {
        refreshActivityTimelineInteractor.refreshActivityTimeline(callback, errorCallback);
    }

    @Override public void obtainOlderTimeline(long currentOldestDate, Interactor.Callback<ActivityTimeline> callback,
      Interactor.ErrorCallback errorCallback) {
        getOlderActivityTimelineInteractor.loadOlderActivityTimeline(currentOldestDate, callback, errorCallback);
    }
}
