package com.shootr.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SetWatchingInfoOnlineJob extends ShootrBaseJob {

    private static final int PRIORITY = 9;
    private WatchManager watchManager;
    private ShootrService service;

    @Inject
    protected SetWatchingInfoOnlineJob( Application application, Bus bus, NetworkUtil networkUtil, WatchManager watchManager, ShootrService service, SQLiteOpenHelper openHelper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.watchManager = watchManager;
        this.service = service;
        this.setOpenHelper(openHelper);

    }

    @Override protected void createDatabase() {
        createWritableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        watchManager.setDataBase(db);
    }

    @Override protected void run() throws SQLException, IOException {
        checkIfWeHaveSomeChangesInWatchAndSendToServer();
    }

    private void checkIfWeHaveSomeChangesInWatchAndSendToServer() throws IOException {
        List<WatchEntity> watchesToUpdate = watchManager.getDatasForSendToServerInCase();
         List<WatchEntity> watchEntities = new ArrayList<>();
        if (watchesToUpdate.size() > 0) {
            for (WatchEntity watch : watchesToUpdate) {
                WatchEntity watchEntity = setWatch(watch);
                if(watchEntity!=null) watchEntities.add(watchEntity);
            }
        }
        watchManager.saveWatches(watchEntities);
    }

    public WatchEntity setWatch(WatchEntity watchEntity) throws IOException {
        return service.setWatchStatus(watchEntity);
    }


    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
