package com.shootr.android.task.jobs;

import android.app.Application;
import android.content.Context;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.squareup.otto.Bus;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.DatabaseErrorEvent;
import java.io.IOException;
import java.sql.SQLException;
import timber.log.Timber;

public abstract class ShootrBaseJob<T> extends Job {

    private final Application application;

    private Bus bus;
    private NetworkUtil networkUtil;

    protected ShootrBaseJob(Params params, Application application, @Main Bus bus, NetworkUtil networkUtil) {
        super(params);
        this.application = application;
        this.bus = bus;
        this.networkUtil = networkUtil;
    }

    @Override
    public void onRun() throws Throwable {
        if (!hasInternetConnection()) {
            postConnectionNotAvailableEvent();
            if (isNetworkRequired()) {
                return;
            }
        }

        try {
            run();
        } catch (SQLException e) {
            Timber.e(e, "SQLException executing job");
            postDatabaseErrorEvent();
        } catch (IOException e) { // includes ServerException
            Timber.e(e, "IOException executing job");
            postCommunicationErrorEvent(e);
        }
    }

    public boolean hasInternetConnection(){
        return networkUtil.isConnected(application);
    }

    protected abstract void run() throws SQLException, IOException, Exception;

    protected void postSuccessfulEvent(T result) {
        bus.post(result);
    }

    private void postConnectionNotAvailableEvent() {
        bus.post(new ConnectionNotAvailableEvent());
    }

    private void postDatabaseErrorEvent() {
        bus.post(new DatabaseErrorEvent());
    }

    private void postCommunicationErrorEvent(IOException e) {
        bus.post(new CommunicationErrorEvent(e));
    }

    protected void postCustomEvent(Object o) {
        bus.post(o);
    }

    protected abstract boolean isNetworkRequired();

    @Override public void onAdded() {
    }

    @Override protected void onCancel() {
    }

    @Override protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    protected Context getContext() {
        return application;
    }
    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public void setNetworkUtil(NetworkUtil networkUtil) {
        this.networkUtil = networkUtil;
    }

    public static class SuccessEvent<T> {

        private T result;

        public SuccessEvent(T result) {
            this.result = result;
        }

        public T getResult() {
            return result;
        }
    }

}
