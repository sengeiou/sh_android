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

public class LocalActivityRepository implements ActivityRepository {

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
        List<ActivityEntity> activityTimeline = localActivityDataSource.getActivityTimeline(parameters, locale);
        List<ActivityEntity> activityEntities = bindActivityShots(activityTimeline, parameters);
        return activityEntityMapper.transform(activityEntities);
    }

    @Override public void deleteActivitiesWithShot(String idShot) {
        localActivityDataSource.deleteActivitiesWithShot(idShot);
    }

    private List<ActivityEntity> bindActivityShots(List<ActivityEntity> activities,
        ActivityTimelineParameters parameters) {
        List<ActivityEntity> activityEntities = new ArrayList<>();
        for (ActivityEntity activity : activities) {
            bindActivityShot(activity, parameters);
            if (activity.getIdShot() != null && activity.getShotForMapping() != null) {
                activityEntities.add(activity);
            } else if (activity.getIdShot() == null) {
                activityEntities.add(activity);
            }
        }
        return activityEntities;
    }

    private void bindActivityShot(ActivityEntity activity, ActivityTimelineParameters parameters) {
        if (activity.getIdShot() != null) {
            activity.setShotForMapping(getShotForActivity(activity, parameters));
        }
    }

    private Shot getShotForActivity(ActivityEntity activity, ActivityTimelineParameters parameters) {
        String idShot = checkNotNull(activity.getIdShot());
        return localShotRepository.getShot(idShot, parameters.getStreamTypes(), parameters.getShotTypes());
    }
}
