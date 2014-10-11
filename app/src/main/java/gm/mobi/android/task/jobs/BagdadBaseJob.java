package gm.mobi.android.task.jobs;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.task.events.CommunicationErrorEvent;
import gm.mobi.android.task.events.ConnectionNotAvailableEvent;
import gm.mobi.android.task.events.DatabaseErrorEvent;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import timber.log.Timber;

public abstract class BagdadBaseJob<T> extends Job {

    private final Application application;

    private SQLiteOpenHelper dbHelper;

    private Bus bus;
    private NetworkUtil networkUtil;
    private SQLiteDatabase db;

    protected BagdadBaseJob(Params params, Application application, Bus bus, NetworkUtil networkUtil) {
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
        if (isNetworkRequired() && !networkUtil.isConnected(application)) {
            postNetworkNotAvailableEvent();
            return;
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
        SuccessEvent<T> successEvent = new SuccessEvent<>(result);
        bus.post(successEvent);
    }

    private void postNetworkNotAvailableEvent() {
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

    public static class SuccessEvent<T> {

        public T result;

        public SuccessEvent(T result) {
            this.result = result;
        }
    }

}
