package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.task.events.CommunicationErrorEvent;
import com.shootr.mobile.task.events.ConnectionNotAvailableEvent;
import com.squareup.otto.Subscribe;

public interface CommunicationPresenter {

    @Subscribe void onCommunicationError(CommunicationErrorEvent event);

    @Subscribe void onConnectionNotAvailable(ConnectionNotAvailableEvent event);
}
