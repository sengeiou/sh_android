package com.shootr.android.ui.presenter;

import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.squareup.otto.Subscribe;

public interface CommunicationPresenter {

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event);

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event);
}
