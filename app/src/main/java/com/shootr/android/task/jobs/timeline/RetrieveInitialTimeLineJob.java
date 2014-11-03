package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.timeline.ShotsResultEvent;
import com.shootr.android.ui.model.ShotModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class RetrieveInitialTimeLineJob  extends TimelineJob<ShotsResultEvent>{

    private ShotManager shotManager;
    private ShootrService service;
    private FollowManager followManager;
    private UserEntity currentUser;

    @Inject public RetrieveInitialTimeLineJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(context, bus, service, networkUtil, shotManager, followManager, dbHelper);
        this.shotManager = shotManager;
        this.service = service;
        this.followManager = followManager;
    }

    @Override public void init(UserEntity currentUser) {
        super.init(currentUser);
        this.currentUser = currentUser;
    }

    @Override protected void run() throws SQLException, IOException {
        List<ShotEntity> remoteShots = service.getShotsByUserIdList(getFollowingIds(), 0L);
        shotManager.saveShots(remoteShots);
        // Retrieve from db because we need the user objects associated to the shots
        List<ShotModel> shotsWithUsersFromServer = shotManager.retrieveTimelineWithUsers(currentUser.getIdUser());
        postSuccessfulEvent(new ShotsResultEvent(shotsWithUsersFromServer));
   }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
