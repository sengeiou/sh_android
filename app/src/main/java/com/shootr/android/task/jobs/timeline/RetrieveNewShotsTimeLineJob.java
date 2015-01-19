package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.gcm.event.RequestWatchByPushEvent;
import com.squareup.otto.Bus;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.timeline.NewShotsReceivedEvent;
import com.shootr.android.ui.model.ShotModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;

public class RetrieveNewShotsTimeLineJob extends TimelineJob<NewShotsReceivedEvent>{

    private final Bus bus;
    private ShotManager shotManager;
    private ShootrService service;

    @Inject public RetrieveNewShotsTimeLineJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SessionRepository sessionRepository) {
        super(context, bus, networkUtil, followManager, sessionRepository);
        this.bus = bus;
        this.shotManager = shotManager;
        this.service = service;
    }

    @Override protected void run() throws SQLException, IOException {
        List<ShotModel> updatedTimeline;
        List<ShotEntity> newShots = new ArrayList<>();

        Long lastModifiedDate = shotManager.getLastModifiedDate(DatabaseContract.ShotTable.TABLE);
        if(!getFollowingIds().isEmpty()) {
             newShots = service.getNewShots(getFollowingIds(), lastModifiedDate);
            detectNotWatchingAndNotify(newShots);
            //TODO what if newshots is empty?
            shotManager.saveShots(newShots);
        }
        updatedTimeline = shotManager.retrieveTimelineWithUsers();
        updatedTimeline = filterShots(updatedTimeline);
        postSuccessfulEvent(new NewShotsReceivedEvent(updatedTimeline, newShots.size()));
    }

    /**
     * Nota: No me gusta nada de nada hacer esto así ¬¬
     * @author Rafa
     */
    private void detectNotWatchingAndNotify(List<ShotEntity> newShots) {
        for (ShotEntity newShot : newShots) {
            if (newShot.getType() == ShotEntity.TYPE_WATCH_NEGATIVE) {
                bus.post(new RequestWatchByPushEvent());
                break;
            }
        }
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
