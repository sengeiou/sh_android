package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.mapper.ActivityEntityMapper;
import com.shootr.android.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncActivityRepository implements ActivityRepository {

    private final ActivityDataSource localActivityDataSource;
    private final ActivityDataSource remoteActivityDataSource;
    private final ActivityEntityMapper activityEntityMapper;
    private final SessionRepository sessionRepository;

    @Inject public SyncActivityRepository(@Local ActivityDataSource localActivityDataSource,
      @Remote ActivityDataSource remoteActivityDataSource, ActivityEntityMapper activityEntityMapper,
      SessionRepository sessionRepository) {
        this.localActivityDataSource = localActivityDataSource;
        this.remoteActivityDataSource = remoteActivityDataSource;
        this.activityEntityMapper = activityEntityMapper;
        this.sessionRepository = sessionRepository;
    }

    @Override public List<Activity> getActivityTimeline(ActivityTimelineParameters parameters) {
        String currentUserId = sessionRepository.getCurrentUserId();
        List<ActivityEntity> activityEntities = remoteActivityDataSource.getActivityTimeline(parameters, currentUserId);
        localActivityDataSource.putActivities(activityEntities);
        return activityEntityMapper.transform(activityEntities);
    }
}
