package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.mapper.ActivityEntityMapper;
import com.shootr.android.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class LocalActivityRepository implements ActivityRepository{

    private final ActivityDataSource localActivityDataSource;
    private final ActivityEntityMapper activityEntityMapper;
    private final LocalShotRepository localShotRepository;
    private final LocalStreamRepository localStreamRepository;

    @Inject public LocalActivityRepository(@Local ActivityDataSource localActivityDataSource,
      ActivityEntityMapper activityEntityMapper, LocalShotRepository localShotRepository,
      LocalStreamRepository localStreamRepository) {
        this.localActivityDataSource = localActivityDataSource;
        this.activityEntityMapper = activityEntityMapper;
        this.localShotRepository = localShotRepository;
        this.localStreamRepository = localStreamRepository;
    }

    @Override public List<Activity> getActivityTimeline(ActivityTimelineParameters parameters) {
        List<ActivityEntity> activityTimeline =
          localActivityDataSource.getActivityTimeline(parameters);
        List<ActivityEntity> activityEntities = bindActivityShots(activityTimeline);
        return activityEntityMapper.transform(activityEntities);
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

    private List<ActivityEntity> bindActivityShots(List<ActivityEntity> activities) {
        List<ActivityEntity> activityEntities = new ArrayList<>();
        for (ActivityEntity activity : activities) {
            bindActivityShot(activity);
            if(activity.getIdShot()!=null && activity.getShotForMapping() != null) {
                activityEntities.add(activity);
            } else if (activity.getIdShot() == null) {
                activityEntities.add(activity);
            }
        }
        return activityEntities;
    }

    private void bindActivityShot(ActivityEntity activity) {
        if (activity.getIdShot() != null) {
            activity.setShotForMapping(getShotForActivity(activity));
        }
    }

    private Shot getShotForActivity(ActivityEntity activity) {
        String idShot = checkNotNull(activity.getIdShot());
        return localShotRepository.getShot(idShot);
    }
}
