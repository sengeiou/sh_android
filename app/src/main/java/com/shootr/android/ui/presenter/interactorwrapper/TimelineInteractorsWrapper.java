package com.shootr.android.ui.presenter.interactorwrapper;

import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.interactor.Interactor;

public interface TimelineInteractorsWrapper {

    void loadTimeline(Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback);

    void refreshTimeline(Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback);

    void obtainOlderTimeline(long lastShotInScreenDate, Interactor.Callback<Timeline> callback, Interactor.ErrorCallback errorCallback);

}
