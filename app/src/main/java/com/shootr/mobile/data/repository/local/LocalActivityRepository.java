package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.data.mapper.ActivityEntityMapper;
import com.shootr.mobile.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

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

    @Override public List<Activity> getActivityTimeline(ActivityTimelineParameters parameters, String locale) {
        List<ActivityEntity> activityTimeline =
          localActivityDataSource.getActivityTimeline(parameters, locale);
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
