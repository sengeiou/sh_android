package com.shootr.android.task;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.DatabaseErrorEvent;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import timber.log.Timber;

public class JobWrapper extends Job {

    private final Interactor interactor;
    private final Bus bus;
    private final NetworkConnection networkConnection;

    private boolean requiresNetwork = false;

    public JobWrapper(Interactor interactor, Bus bus, NetworkConnection networkConnection) {
        super(new Params(5));
        this.interactor = interactor;
        this.bus = bus;
        this.networkConnection = networkConnection;
    }

    @Override public void onRun() throws Throwable {
        if (!hasInternetConnection()) {
            postConnectionNotAvailableEvent();
            if (requiresNetwork) {
                return;
            }
        }

        try {
            executeInteractor();
        } catch (SQLException e) {
            Timber.e(e, "SQLException executing job");
            postDatabaseErrorEvent();
        } catch (IOException e) { // includes ServerException
            Timber.e(e, "IOException executing job");
            postCommunicationErrorEvent(e);
        }
    }

    private void executeInteractor() throws Throwable {
        interactor.execute();
    }

    /**
     * @deprecated The responsability for network usage should not be on the presentation layer
     * nor the domain layer. It should be on the repository.
     *
     */
    @Deprecated
    public void setRequiresNetwork(boolean requiresNetwork) {
        this.requiresNetwork = requiresNetwork;
    }

    private void sendResultToPresentation(Object result) {
        bus.post(result);
    }

    private void postCommunicationErrorEvent(IOException e) {
        bus.post(new CommunicationErrorEvent(e));
    }

    private void postDatabaseErrorEvent() {
        bus.post(new DatabaseErrorEvent());
    }

    private void postConnectionNotAvailableEvent() {
        bus.post(new ConnectionNotAvailableEvent());
    }

    public boolean hasInternetConnection(){
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
}
