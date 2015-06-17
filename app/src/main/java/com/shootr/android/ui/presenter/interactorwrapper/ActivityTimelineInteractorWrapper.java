package com.shootr.android.ui.presenter.interactorwrapper;

import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.interactor.Interactor;

public interface ActivityTimelineInteractorWrapper {

    void loadTimeline(Interactor.Callback<ActivityTimeline> callback, Interactor.ErrorCallback errorCallback);

    void refreshTimeline(Interactor.Callback<ActivityTimeline> callback, Interactor.ErrorCallback errorCallback);

    void obtainOlderTimeline(long lastShotInScreenDate, Interactor.Callback<ActivityTimeline> callback, Interactor.ErrorCallback errorCallback);

}
