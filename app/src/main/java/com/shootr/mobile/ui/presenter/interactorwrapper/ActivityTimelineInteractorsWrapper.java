package com.shootr.mobile.ui.presenter.interactorwrapper;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.timeline.activity.GetActivityTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.activity.GetOlderActivityTimelineInteractor;
import com.shootr.mobile.domain.interactor.timeline.activity.RefreshActivityTimelineInteractor;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.utils.LocaleProvider;
import javax.inject.Inject;

public class ActivityTimelineInteractorsWrapper {

    private final RefreshActivityTimelineInteractor refreshActivityTimelineInteractor;
    private final GetActivityTimelineInteractor getActivityTimelineInteractor;
    private final GetOlderActivityTimelineInteractor getOlderActivityTimelineInteractor;
    private final LocaleProvider localeProvider;

    @Inject
    public ActivityTimelineInteractorsWrapper(RefreshActivityTimelineInteractor refreshActivityTimelineInteractor,
      GetActivityTimelineInteractor getActivityTimelineInteractor,
      GetOlderActivityTimelineInteractor getOlderActivityTimelineInteractor, LocaleProvider localeProvider) {
        this.refreshActivityTimelineInteractor = refreshActivityTimelineInteractor;
        this.getActivityTimelineInteractor = getActivityTimelineInteractor;
        this.getOlderActivityTimelineInteractor = getOlderActivityTimelineInteractor;
        this.localeProvider = localeProvider;
    }

    public void loadTimeline(Boolean isUserActivityTimeline, Interactor.Callback<ActivityTimeline> callback) {
        getActivityTimelineInteractor.loadActivityTimeline(isUserActivityTimeline,
          localeProvider.getLocale(),
          callback);
    }

    public void refreshTimeline(Boolean isUserActivityTimeline, Interactor.Callback<ActivityTimeline> callback,
      Interactor.ErrorCallback errorCallback) {
        refreshActivityTimelineInteractor.refreshActivityTimeline(isUserActivityTimeline,
          localeProvider.getLocale(),
          callback,
          errorCallback);
    }

    public void obtainOlderTimeline(Boolean isUserActivityTimeline, long currentOldestDate,
      Interactor.Callback<ActivityTimeline> callback, Interactor.ErrorCallback errorCallback) {
        getOlderActivityTimelineInteractor.loadOlderActivityTimeline(isUserActivityTimeline,
          currentOldestDate,
          localeProvider.getLocale(),
          callback,
          errorCallback);
    }
}
