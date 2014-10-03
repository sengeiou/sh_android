package gm.mobi.android.task.jobs;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;

import timber.log.Timber;

public abstract class CancellableJob extends Job {


    private boolean cancelled;

    @Inject
    SQLiteOpenHelper dbHelper;
    protected SQLiteDatabase db;


    protected CancellableJob(Params params) {
        super(params);
    }


    protected void createReadableDb(){
        db = dbHelper.getReadableDatabase();
    }

    protected void createWritableDb(){
        db = dbHelper.getWritableDatabase();
    }

    protected void configureManagers(){
        createDatabase();
        setDatabaseToManagers();
    }

    protected abstract void createDatabase();
    protected abstract void setDatabaseToManagers();

    protected abstract void run() throws SQLException, IOException;

    @Override
    public void onRun() throws Throwable {
        try {
            configureManagers();
            run();
        } catch (SQLException e) {
            //TODO server error
            Timber.e("SQLException Message: %s, Stacktrace : %s", e.getMessage(), e.getStackTrace());
        }catch(IOException e){
            Timber.e("IOException Message: %s, Stacktrace: %s", e.getMessage(), e.getStackTrace());
        }finally {
            closeDb();
        }
    }

    /**
     * Set job to cancelled, so it won't return any value or send any event.
     * Useful for avoiding receiving
     * Note: doesn't stops it, just sets a flag. Implementation is responsible for checking it before performing work or returning a response.
     */
    public void cancelJob() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    private void closeDb(){
        if(db != null){
            db.close();
        }
    }



}
