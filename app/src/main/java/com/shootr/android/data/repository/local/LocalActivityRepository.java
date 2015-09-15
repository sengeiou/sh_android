package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.mapper.ActivityEntityMapper;
import com.shootr.android.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class LocalActivityRepository implements ActivityRepository{

    private final ActivityDataSource localActivityDataSource;
    private final ActivityEntityMapper activityEntityMapper;
    private final LocalShotRepository localShotRepository;

    @Inject public LocalActivityRepository(@Local ActivityDataSource localActivityDataSource,
      ActivityEntityMapper activityEntityMapper, LocalShotRepository localShotRepository) {
        this.localActivityDataSource = localActivityDataSource;
        this.activityEntityMapper = activityEntityMapper;
        this.localShotRepository = localShotRepository;
    }

    @Override public List<Activity> getActivityTimeline(ActivityTimelineParameters parameters) {
        List<ActivityEntity> activityTimeline =
          localActivityDataSource.getActivityTimeline(parameters);
        bindActivityShots(activityTimeline);
        return activityEntityMapper.transform(activityTimeline);
    }

    @Override
    public Activity getActivity(String activityId) {
        ActivityEntity activity = localActivityDataSource.getActivity(activityId);
        bindActivityShot(activity);
        return activityEntityMapper.transform(activity);
    }

    @Override public void deleteActivitiesWithShot(String idShot) {
        localActivityDataSource.deleteActivitiesWithShot(idShot);
    }

    private void bindActivityShots(List<ActivityEntity> activities) {
        for (ActivityEntity activity : activities) {
            bindActivityShot(activity);
        }
    }

    private void bindActivityShot(ActivityEntity activity) {
        if (activity.getIdShot() != null) {
            activity.setShotForMapping(getShotForActivity(activity));
        }
    }

    private Shot getShotForActivity(ActivityEntity activity) {
        String idShot = checkNotNull(activity.getIdShot());
        Shot shot = localShotRepository.getShot(idShot);
        checkNotNull(shot, "Shot with id %s not found in local repository", idShot);
        return shot;
    }
}
