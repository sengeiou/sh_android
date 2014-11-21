package com.shootr.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
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
import timber.log.Timber;

public class DeleteMatchOnlineJob extends ShootrBaseJob<Void> {

    public static final int PRIORITY = 9;
    private WatchManager watchManager;
    private ShootrService service;

    @Inject public DeleteMatchOnlineJob(Application application, Bus bus, NetworkUtil networkUtil, WatchManager watchManager, ShootrService service) {
        super(new Params(PRIORITY).groupBy("info").requireNetwork(), application, bus, networkUtil);
        this.watchManager = watchManager;
        this.service = service;
    }


    @Override protected void run() throws Exception {
        checkIfWeHaveSomeChangesInWatchAndSendToServer();
    }

    private void checkIfWeHaveSomeChangesInWatchAndSendToServer() throws IOException {
        List<WatchEntity> watchesNotSynchronized = watchManager.getEntitiesNotSynchronizedWithServer();
        //TODO hacer en 1 llamada
        for (WatchEntity watchToSend : watchesNotSynchronized) {
            WatchEntity watchReceived = service.setWatchStatus(watchToSend);
            watchManager.saveWatch(watchReceived);
        }
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
