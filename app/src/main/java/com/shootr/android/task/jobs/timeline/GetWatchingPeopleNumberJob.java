package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.manager.WatchManager;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.task.events.timeline.WatchingPeopleNumberEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class GetWatchingPeopleNumberJob extends ShootrBaseJob<WatchingPeopleNumberEvent> {

    private static final int PRIORITY = 9;
    private final WatchManager watchManager;
    private final SessionRepository sessionRepository;

    @Inject protected GetWatchingPeopleNumberJob(Application application, Bus bus, NetworkUtil networkUtil, WatchManager watchManager,
      SessionRepository sessionRepository) {
        super(new Params(PRIORITY).groupBy("info"), application, bus, networkUtil);
        this.watchManager = watchManager;
        this.sessionRepository = sessionRepository;
    }

    @Override protected void run() throws SQLException, IOException {
        Integer number = getNumberOfWatchers();

        postSuccessfulEvent(new WatchingPeopleNumberEvent(number));
    }

    public Integer getNumberOfWatchers() {
        WatchEntity watching = watchManager.getWatchVisibleByUser(sessionRepository.getCurrentUserId());
        if (watching != null) {
            List<WatchEntity> watchesByMatch = watchManager.getWatchesByMatch(watching.getIdMatch());
            return watchesByMatch.size();
        } else {
            return 0;
        }
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
