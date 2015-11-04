package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.task.events.CommunicationErrorEvent;
import com.shootr.mobile.task.events.ConnectionNotAvailableEvent;
import com.squareup.otto.Subscribe;

public interface CommunicationPresenter {

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event);

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event);
}
