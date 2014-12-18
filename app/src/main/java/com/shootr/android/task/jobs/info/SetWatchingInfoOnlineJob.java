package com.shootr.android.task.jobs.info;

import android.app.Application;
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
import timber.log.Timber;

public class SetWatchingInfoOnlineJob extends ShootrBaseJob {

    private static final int PRIORITY = 9;
    private WatchManager watchManager;
    private ShootrService service;

    @Inject
    protected SetWatchingInfoOnlineJob(Application application, Bus bus, NetworkUtil networkUtil,
      WatchManager watchManager, ShootrService service) {
        super(new Params(PRIORITY).requireNetwork(), application, bus, networkUtil);
        this.watchManager = watchManager;
        this.service = service;
    }

    @Override protected void run() throws SQLException, IOException {
        checkIfWeHaveSomeChangesInWatchAndSendToServer();
    }

    private void checkIfWeHaveSomeChangesInWatchAndSendToServer() {
        List<WatchEntity> watchesToUpdate = watchManager.getEntitiesNotSynchronizedWithServer();
        List<WatchEntity> watchEntities = new ArrayList<>();
        if (!watchesToUpdate.isEmpty()) {
            for (WatchEntity watch : watchesToUpdate) {
                WatchEntity watchEntity = setWatch(watch);
                if (watchEntity != null) {
                    watchEntities.add(watchEntity);
                }
            }
        }
        watchManager.saveWatches(watchEntities);
    }

    public WatchEntity setWatch(WatchEntity watchEntity) {
        try {
            return service.setWatchStatus(watchEntity);
        } catch (IOException e) {
            Timber.e(e, "Error sending watch to server with matchId=%d", watchEntity.getIdMatch());
            return null;
        }
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
