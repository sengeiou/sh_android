package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.db.objects.WatchEntity;
import com.squareup.otto.Bus;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.timeline.NewShotsReceivedEvent;
import com.shootr.android.ui.model.ShotModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;

public class RetrieveNewShotsTimeLineJob extends TimelineJob<NewShotsReceivedEvent>{

    private ShotManager shotManager;
    private ShootrService service;
    private UserEntity currentUser;

    @Inject public RetrieveNewShotsTimeLineJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SQLiteOpenHelper dbHelper) {
        super(context, bus, service, networkUtil, shotManager, followManager, dbHelper);
        this.shotManager = shotManager;
        this.service = service;
    }

    @Override public void init(UserEntity currentUser) {

        super.init(currentUser);
        this.currentUser = currentUser;
    }

    @Override protected void run() throws SQLException, IOException {
        List<ShotModel> updatedTimeline = new ArrayList<>();
        List<ShotEntity> newShots = new CopyOnWriteArrayList<>();

        Long lastModifiedDate = shotManager.getLastModifiedDate(DatabaseContract.ShotTable.TABLE);
        if(!getFollowingIds().isEmpty()) {
             newShots = service.getNewShots(getFollowingIds(), lastModifiedDate);
            //TODO what if newshots is empty?
            shotManager.saveShots(newShots);
        }
        updatedTimeline = shotManager.retrieveTimelineWithUsers(currentUser.getIdUser());
        postSuccessfulEvent(new NewShotsReceivedEvent(updatedTimeline, newShots.size()));
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
