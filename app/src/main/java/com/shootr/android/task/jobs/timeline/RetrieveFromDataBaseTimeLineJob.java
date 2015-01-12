package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.domain.repository.SessionRepository;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.timeline.ShotsResultEvent;
import com.shootr.android.ui.model.ShotModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class RetrieveFromDataBaseTimeLineJob  extends TimelineJob<ShotsResultEvent>{

    private ShotManager shotManager;
    private SessionRepository sessionRepository;

    @Inject public RetrieveFromDataBaseTimeLineJob(Application context, Bus bus, ShootrService service, NetworkUtil networkUtil,
      ShotManager shotManager, FollowManager followManager, SessionRepository sessionRepository) {
        super(context, bus, networkUtil, followManager, sessionRepository);
        this.shotManager = shotManager;
        this.sessionRepository = sessionRepository;
    }

    @Override protected void run() throws SQLException, IOException {

        List<ShotModel> localShots = shotManager.retrieveTimelineWithUsers(sessionRepository.getCurrentUserId());
        if (localShots != null && !localShots.isEmpty()) {
            postSuccessfulEvent(new ShotsResultEvent(localShots));
        }
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
