package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.repository.SessionRepository;
import com.squareup.otto.Bus;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.data.entity.ShotEntity;
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
    private SessionRepository sessionRepository;

    @Inject public RetrieveOldShotsTimeLineJob(Application context, @Main Bus bus, ShootrService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SessionRepository sessionRepository) {
        super(context, bus, networkUtil, followManager, sessionRepository);
        this.shotManager = shotManager;
        this.service = service;
        this.sessionRepository = sessionRepository;
    }


    @Override protected void run() throws SQLException, IOException {
        Long firstModifiedDate = shotManager.getFirstModifiedDate(DatabaseContract.ShotTable.TABLE);
        List<ShotEntity> olderShots = service.getOlderShots(getFollowingIds(), firstModifiedDate);
        shotManager.saveShots(olderShots);
        List<ShotModel> olderShotsWithUsers = shotManager.retrieveOldOrNewTimeLineWithUsers(olderShots, sessionRepository.getCurrentUserId());
        olderShotsWithUsers = filterShots(olderShotsWithUsers);
        postSuccessfulEvent(new OldShotsReceivedEvent(olderShotsWithUsers));
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
