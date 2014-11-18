package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.db.manager.MatchManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.task.events.timeline.WatchingPeopleNumberEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;

public class GetWatchingPeopleNumberJob extends ShootrBaseJob<WatchingPeopleNumberEvent> {

    private static final int PRIORITY = 9;
    WatchManager watchManager;
    MatchManager matchManager;

    @Inject protected GetWatchingPeopleNumberJob(Application application, Bus bus, NetworkUtil networkUtil, SQLiteOpenHelper openHelper, WatchManager watchManager, MatchManager matchManager) {
        super(new Params(PRIORITY).groupBy("info"), application, bus, networkUtil);
        this.watchManager = watchManager;
        this.matchManager = matchManager;
        setOpenHelper(openHelper);
    }

    @Override protected void createDatabase() {
        createReadableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        watchManager.setDataBase(db);
        matchManager.setDataBase(db);
    }

    @Override protected void run() throws SQLException, IOException {
        Integer number = getNumberOfWatchers();

        postSuccessfulEvent(new WatchingPeopleNumberEvent(number));
    }

    public Integer getNumberOfWatchers(){
        return watchManager.getPeopleWatchingInInfo();
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
