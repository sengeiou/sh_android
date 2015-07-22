package com.shootr.android.ui.presenter;

import com.shootr.android.task.events.CommunicationErrorStream;
import com.shootr.android.task.events.ConnectionNotAvailableStream;
import com.squareup.otto.Subscribe;

public interface CommunicationPresenter {

    @Subscribe
    public void onCommunicationError(CommunicationErrorStream event);

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableStream event);
}
