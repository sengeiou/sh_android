package com.shootr.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.service.textservice.SpellCheckerService;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.SessionManager;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.db.objects.WatchEntity;
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
    private Long idMatch;
    private SessionManager sessionManager;

    @Inject
    protected SetWatchingInfoOfflineJob(Application application, Bus bus, NetworkUtil networkUtil,
      SQLiteOpenHelper openHelper, WatchManager watchManager, SessionManager sessionManager) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.sessionManager = sessionManager;
        this.setOpenHelper(openHelper);
        this.watchManager = watchManager;
    }

    public void init(Long idMatch, Long status){
        this.idMatch = idMatch;
        this.status = status;
    }


    @Override protected void createDatabase() {
        createWritableDb();
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        watchManager.setDataBase(db);
    }

    @Override protected void run() throws SQLException, IOException {
        createUpdateWatchEntityFromDB();

    }

    public WatchEntity createUpdateWatchEntityFromDB(){
        Date date = new Date(System.currentTimeMillis());
        WatchEntity watchEntity = watchManager.getWatchByKeys(sessionManager.getCurrentUserId(), idMatch);
        if(watchEntity==null){
            watchEntity = new WatchEntity();
            watchEntity.setCsys_birth(date);
            watchEntity.setCsys_modified(date);
            watchEntity.setCsys_revision(0);
            watchEntity.setCsys_synchronized("N");
        }else{
            watchEntity.setCsys_modified(date);
            watchEntity.setCsys_revision(watchEntity.getCsys_revision()+1);
            watchEntity.setCsys_synchronized("U");
        }
        watchEntity.setIdUser(sessionManager.getCurrentUserId());
        watchEntity.setIdMatch(idMatch);
        watchEntity.setStatus(status);
        watchManager.createUpdateWatch(watchEntity);
        return watchEntity;
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
