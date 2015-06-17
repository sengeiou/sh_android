package com.shootr.android.data.repository.local;

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

    public static final int MAX_ACTIVITIES = 100;

    private final ActivityDataSource localActivityDataSource;
    private final ActivityEntityMapper activityEntityMapper;
    private final SessionRepository sessionRepository;

    @Inject public LocalActivityRepository(@Local ActivityDataSource localActivityDataSource,
      ActivityEntityMapper activityEntityMapper, SessionRepository sessionRepository) {
        this.localActivityDataSource = localActivityDataSource;
        this.activityEntityMapper = activityEntityMapper;
        this.sessionRepository = sessionRepository;
    }

    @Override public List<Activity> getShotsForActivityTimeline(ActivityTimelineParameters parameters) {
        String currentUserId = sessionRepository.getCurrentUserId();
        localActivityDataSource.getActivityTimeline(parameters,currentUserId, MAX_ACTIVITIES);
        return null;
    }
}
