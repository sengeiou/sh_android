package com.shootr.android.interactor;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.task.NetworkConnection;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.squareup.otto.Bus;

public class JobWrapper extends Job {

    private final Interactor interactor;
    private final Bus bus;
    private final NetworkConnection networkConnection;
    private final ErrorCallback errorCallback;

    private boolean requiresNetwork = false;

    public JobWrapper(Interactor interactor, Bus bus, NetworkConnection networkConnection, ErrorCallback errorCallback) {
        super(new Params(5));
        this.interactor = interactor;
        this.bus = bus;
        this.networkConnection = networkConnection;
        this.errorCallback = errorCallback;
    }

    @Override public void onRun() {
        if (!hasInternetConnection()) {
            postConnectionNotAvailableEvent();
            if (requiresNetwork) {
                return;
            }
        }

        try {
            executeInteractor();
        } catch (Throwable throwable) {
            errorCallback.onError(throwable);
        }
    }

    private void executeInteractor() throws Throwable {
        interactor.execute();
    }

    /**
     * @deprecated The responsability for network usage should not be on the presentation layer
     * nor the domain layer. It should be on the repository.
     */
    @Deprecated
    public void setRequiresNetwork(boolean requiresNetwork) {
        this.requiresNetwork = requiresNetwork;
    }

    private void postConnectionNotAvailableEvent() {
        bus.post(new ConnectionNotAvailableEvent());
    }

    public boolean hasInternetConnection() {
        return networkConnection.isConnected();
    }

    @Override public void onAdded() {
        /* no-op */
    }

    @Override protected void onCancel() {
        /* no-op */
    }

    @Override protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    static interface ErrorCallback {

        void onError(Throwable throwable);
    }
}
