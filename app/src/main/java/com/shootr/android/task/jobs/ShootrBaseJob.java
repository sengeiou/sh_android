package com.shootr.android.task.jobs;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.task.events.DatabaseErrorEvent;
import java.io.IOException;
import java.sql.SQLException;
import timber.log.Timber;

public abstract class ShootrBaseJob<T> extends Job {

    private final Application application;

    private SQLiteOpenHelper dbHelper;

    private Bus bus;
    private NetworkUtil networkUtil;
    private SQLiteDatabase db;

    protected ShootrBaseJob(Params params, Application application, Bus bus, NetworkUtil networkUtil) {
        super(params);
        this.application = application;
        this.bus = bus;
        this.networkUtil = networkUtil;
    }

    public void setOpenHelper(SQLiteOpenHelper openHelper) {
        this.dbHelper = openHelper;
    }

    @Override
    public void onRun() throws Throwable {
        if (!hasInternetConnection()) {
            postConnectionNotAvailableEvent();
            if (isNetworkRequired()) {
                return;
            }
        }

        configureManagers();

        try {
            run();
        } catch (SQLException e) {
            Timber.e(e, "SQLException executing job");
            postDatabaseErrorEvent();
        } catch (IOException e) { // includes ServerException
            Timber.e(e, "IOException executing job");
            postCommunicationErrorEvent();
        } finally {
            closeDb();
        }
    }

    public boolean hasInternetConnection(){
        return networkUtil.isConnected(application);
    }

    protected void configureManagers() {
        createDatabase();
        setDatabaseToManagers(db);
    }

    protected abstract void createDatabase();

    protected abstract void setDatabaseToManagers(SQLiteDatabase db);

    protected void createReadableDb() {
        if (dbHelper == null) {
            throw getDatabaseHelperNotSetException();
        }
        db = dbHelper.getReadableDatabase();
    }

    protected void createWritableDb() {
        if (dbHelper == null) {
            throw getDatabaseHelperNotSetException();
        }
        db = dbHelper.getWritableDatabase();
    }

    private IllegalStateException getDatabaseHelperNotSetException() {
        return new IllegalStateException(
          "This Job is trying to use the database, but no SQLiteOpenHelper was provided. Use the method setSQLiteOpenHelper first.");
    }

    protected abstract void run() throws SQLException, IOException;

    protected void postSuccessfulEvent(T result) {
        bus.post(result);
    }

    private void postConnectionNotAvailableEvent() {
        bus.post(new ConnectionNotAvailableEvent());
    }

    private void postDatabaseErrorEvent() {
        bus.post(new DatabaseErrorEvent());
    }

    private void postCommunicationErrorEvent() {
        bus.post(new CommunicationErrorEvent());
    }

    protected void postCustomEvent(Object o) {
        bus.post(o);
    }

    private void closeDb() {
        if (db != null) {
            db.close();
        }
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

        public T result;

        public SuccessEvent(T result) {
            this.result = result;
        }

        public T getResult() {
            return result;
        }
    }

}