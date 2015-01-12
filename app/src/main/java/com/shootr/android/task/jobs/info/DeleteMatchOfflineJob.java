package com.shootr.android.task.jobs.info;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.util.TimeUtils;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;

public class DeleteMatchOfflineJob extends ShootrBaseJob<Void> {

    public static final int PRIORITY = 10;
    private WatchManager watchManager;
    private SessionRepository sessionRepository;
    private TimeUtils timeUtils;

    private Long idMatch;

    @Inject public DeleteMatchOfflineJob(Application application, Bus bus, NetworkUtil networkUtil, SessionRepository sessionRepository,
      WatchManager watchManager, TimeUtils timeUtils) {
        super(new Params(PRIORITY).groupBy("info"), application, bus, networkUtil);
        this.sessionRepository = sessionRepository;
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
        WatchEntity watchEntity = watchManager.getWatchByKeys(sessionRepository.getCurrentUserId(), idMatch);
        if (watchEntity == null) {
            watchEntity = new WatchEntity();
            watchEntity.setCsysBirth(now);
            watchEntity.setCsysModified(now);
            watchEntity.setCsysRevision(0);
            watchEntity.setCsysSynchronized("N");
            watchEntity.setIdUser(sessionRepository.getCurrentUserId());
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
