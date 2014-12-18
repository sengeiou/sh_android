package com.shootr.android.task.jobs.info;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.WatchEntity;
import com.shootr.android.task.events.info.WatchingInfoResult;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import javax.inject.Inject;

public class SetWatchingInfoOfflineJob extends ShootrBaseJob<WatchingInfoResult> {

    private static final int PRIORITY = 9;
    private WatchManager watchManager;
    private Long status;
    private String place;
    private Long idMatch;
    private SessionManager sessionManager;

    @Inject
    protected SetWatchingInfoOfflineJob(Application application, Bus bus, NetworkUtil networkUtil, WatchManager watchManager, SessionManager sessionManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.sessionManager = sessionManager;
        this.watchManager = watchManager;
    }

    public void init(Long idMatch, Long status, String place){
        this.idMatch = idMatch;
        this.status = status;
        this.place = place;
    }

    @Override protected void run() throws SQLException, IOException {
        createUpdateWatchEntityFromDB();
    }

    public WatchEntity createUpdateWatchEntityFromDB(){
        Date date = new Date(System.currentTimeMillis());
        WatchEntity watchEntity = watchManager.getWatchByKeys(sessionManager.getCurrentUserId(), idMatch);
        if(watchEntity==null){
            watchEntity = new WatchEntity();
            watchEntity.setCsysBirth(date);
            watchEntity.setCsysModified(date);
            watchEntity.setCsysRevision(0);
            watchEntity.setCsysSynchronized("N");
        }else{
            watchEntity.setCsysModified(date);
            watchEntity.setCsysRevision(watchEntity.getCsysRevision() + 1);
            watchEntity.setCsysSynchronized("U");
        }
        watchEntity.setIdUser(sessionManager.getCurrentUserId());
        watchEntity.setIdMatch(idMatch);
        watchEntity.setStatus(status);
        watchEntity.setVisible(true);
        watchEntity.setPlace(place);
        watchManager.createUpdateWatch(watchEntity);
        return watchEntity;
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
