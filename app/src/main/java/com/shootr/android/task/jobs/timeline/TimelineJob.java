package com.shootr.android.task.jobs.timeline;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.ShotModel;
import com.squareup.otto.Bus;
import com.shootr.android.db.manager.FollowManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.task.jobs.ShootrBaseJob.SuccessEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class TimelineJob<T> extends ShootrBaseJob<SuccessEvent> {

    private static final int PRIORITY = 4;

    private FollowManager followManager;
    private SessionRepository sessionRepository;

    public TimelineJob(Application context, Bus bus, NetworkUtil networkUtil, FollowManager followManager,
      SessionRepository sessionRepository) {
        super(new Params(PRIORITY), context, bus, networkUtil);
        this.followManager = followManager;
        this.sessionRepository = sessionRepository;
    }

    public List<Long> getFollowingIds() throws SQLException {
        return followManager.getUserFollowingIdsWithOwnUser(sessionRepository.getCurrentUserId());
    }

    protected List<ShotModel> filterShots(List<ShotModel> updatedTimeline) {
        List<ShotModel> filtered = new ArrayList<>();
        for (ShotModel shotModel : updatedTimeline) {
            if (shotModel.getType() != ShotEntity.TYPE_WATCH_NEGATIVE ) {
                filtered.add(shotModel);
            }
        }
        return filtered;
    }

    @Override protected boolean isNetworkRequired() {
        return false;
    }
}
