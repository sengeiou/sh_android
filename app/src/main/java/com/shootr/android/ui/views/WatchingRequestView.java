package com.shootr.android.ui.views;

import com.shootr.android.ui.model.WatchingRequestModel;

public interface WatchingRequestView{

    void showWatchingRequest(WatchingRequestModel currentRequest);

    void hideWatchingRequest();
}
