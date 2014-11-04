package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.squareup.otto.Bus;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.timeline.OldShotsReceivedEvent;
import com.shootr.android.ui.model.ShotModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class RetrieveOldShotsTimeLineJob extends TimelineJob<OldShotsReceivedEvent> {

    private ShotManager shotManager;
    private ShootrService service;
    private UserEntity currentUser;

    @Inject public RetrieveOldShotsTimeLineJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(context, bus, service, networkUtil, shotManager, followManager, dbHelper);
        this.shotManager = shotManager;
        this.service = service;

    }

    @Override public void init(UserEntity currentUser)
    {
        super.init(currentUser);
        this.currentUser = currentUser;
    }

    @Override protected void run() throws SQLException, IOException {
        Long firstModifiedDate = shotManager.getFirstModifiedDate(DatabaseContract.ShotTable.TABLE);
        List<ShotEntity> olderShots = service.getOlderShots(getFollowingIds(), firstModifiedDate);
        shotManager.saveShots(olderShots);
        List<ShotModel> olderShotsWithUsers = shotManager.retrieveOldOrNewTimeLineWithUsers(olderShots, currentUser.getIdUser());
        //TODO parser Shot to ShotVO
        postSuccessfulEvent(new OldShotsReceivedEvent(olderShotsWithUsers));
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
