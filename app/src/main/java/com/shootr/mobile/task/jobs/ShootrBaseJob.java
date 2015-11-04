package com.shootr.mobile.task.jobs;

import android.app.Application;
import android.content.Context;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.task.events.CommunicationErrorEvent;
import com.shootr.mobile.task.events.ConnectionNotAvailableEvent;
import com.shootr.mobile.task.events.DatabaseErrorEvent;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import timber.log.Timber;

//TODO all children must die
public abstract class ShootrBaseJob<T> implements Interactor {

    private final Application application;

    private Bus bus;

    protected ShootrBaseJob(Application application, Bus bus) {
        this.application = application;
        this.bus = bus;
    }

    @Override
    public void execute() throws Exception {
        onRun();
    }

    public void onRun() throws Exception {
        if (!hasInternetConnection()) {
            postConnectionNotAvailableEvent();
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

    public boolean hasInternetConnection() {
        return true;
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

    protected Context getContext() {
        return application;
    }
    public void setBus(Bus bus) {
        this.bus = bus;
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
