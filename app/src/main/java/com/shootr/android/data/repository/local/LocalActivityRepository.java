package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.mapper.ActivityEntityMapper;
import com.shootr.android.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalActivityRepository implements ActivityRepository{

    private final ActivityDataSource localActivityDataSource;
    private final ActivityEntityMapper activityEntityMapper;
    private final SessionRepository sessionRepository;

    @Inject public LocalActivityRepository(@Local ActivityDataSource localActivityDataSource,
      ActivityEntityMapper activityEntityMapper, SessionRepository sessionRepository) {
        this.localActivityDataSource = localActivityDataSource;
        this.activityEntityMapper = activityEntityMapper;
        this.sessionRepository = sessionRepository;
    }

    @Override public List<Activity> getActivityTimeline(ActivityTimelineParameters parameters) {
        List<ActivityEntity> activityTimeline =
          localActivityDataSource.getActivityTimeline(parameters);
        return activityEntityMapper.transform(activityTimeline);
    }

    @Override
    public Activity getActivity(String activityId) {
        return activityEntityMapper.transform(localActivityDataSource.getActivity(activityId));
    }
}
