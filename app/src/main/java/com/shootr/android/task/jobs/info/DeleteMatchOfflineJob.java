package com.shootr.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.util.TimeUtils;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;
import timber.log.Timber;

public class DeleteMatchOfflineJob extends ShootrBaseJob<Void> {

    public static final int PRIORITY = 10;
    private WatchManager watchManager;
    private SessionManager sessionManager;
    private TimeUtils timeUtils;

    private Long idMatch;

    @Inject public DeleteMatchOfflineJob(Application application, Bus bus, NetworkUtil networkUtil, SessionManager sessionManager,
      WatchManager watchManager, TimeUtils timeUtils) {
        super(new Params(PRIORITY).groupBy("info"), application, bus, networkUtil);
        this.sessionManager = sessionManager;
        this.watchManager = watchManager;
        this.timeUtils = timeUtils;
    }

    public void init(Long idMatch) {
        this.idMatch = idMatch;
    }

    @Override protected void run() throws SQLException, IOException, Exception {
        createUpdateWatchEntityFromDB();
    }

    public WatchEntity createUpdateWatchEntityFromDB() {
        Date now = new Date(timeUtils.getCurrentTime());
        WatchEntity watchEntity = watchManager.getWatchByKeys(sessionManager.getCurrentUserId(), idMatch);
        if (watchEntity == null) {
            watchEntity = new WatchEntity();
            watchEntity.setCsysBirth(now);
            watchEntity.setCsysModified(now);
            watchEntity.setCsysRevision(0);
            watchEntity.setCsysSynchronized("N");
            watchEntity.setIdUser(sessionManager.getCurrentUserId());
            watchEntity.setIdMatch(idMatch);
        } else {
            watchEntity.setCsysModified(now);
            watchEntity.setCsysRevision(watchEntity.getCsysRevision() + 1);
            watchEntity.setCsysSynchronized("U");
        }
        watchEntity.setStatus(WatchEntity.STATUS_REJECT);
        watchEntity.setVisible(false);
        watchManager.createUpdateWatch(watchEntity);
        return watchEntity;
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
