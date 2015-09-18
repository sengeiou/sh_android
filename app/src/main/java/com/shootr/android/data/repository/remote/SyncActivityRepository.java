package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.mapper.ActivityEntityMapper;
import com.shootr.android.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ShotRemovedException;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class SyncActivityRepository implements ActivityRepository {

    private final ActivityDataSource localActivityDataSource;
    private final ActivityDataSource remoteActivityDataSource;
    private final ActivityEntityMapper activityEntityMapper;
    private final ShotRepository remoteShotRepository;
    private final ShotRepository localShotRepository;

    @Inject public SyncActivityRepository(@Local ActivityDataSource localActivityDataSource,
      @Remote ActivityDataSource remoteActivityDataSource,
      ActivityEntityMapper activityEntityMapper,
      @Remote ShotRepository remoteShotRepository,
      @Local ShotRepository localShotRepository) {
        this.localActivityDataSource = localActivityDataSource;
        this.remoteActivityDataSource = remoteActivityDataSource;
        this.activityEntityMapper = activityEntityMapper;
        this.remoteShotRepository = remoteShotRepository;
        this.localShotRepository = localShotRepository;
    }

    @Override public List<Activity> getActivityTimeline(ActivityTimelineParameters parameters) {
        List<ActivityEntity> activityEntities = remoteActivityDataSource.getActivityTimeline(parameters);
        List<ActivityEntity> activities = bindActivityShots(activityEntities);
        localActivityDataSource.putActivities(activities);
        return activityEntityMapper.transform(activities);
    }

    @Override
    public Activity getActivity(String activityId) {
        ActivityEntity activity = remoteActivityDataSource.getActivity(activityId);
        try {
            bindActivityShot(activity);
        } catch (ShotRemovedException e) {
            throw new IllegalArgumentException(e);
        }
        return activityEntityMapper.transform(activity);
    }

    @Override public void deleteActivitiesWithShot(String idShot) {
        throw new IllegalArgumentException("not implemented");
    }

    private List<ActivityEntity> bindActivityShots(List<ActivityEntity> activityEntities) {
        List<ActivityEntity> activities = new ArrayList<>();
        for (ActivityEntity entity : activityEntities) {
            try {
                bindActivityShot(entity);
                activities.add(entity);
            } catch (ShotRemovedException error) {
                /* swallow it */
            }
        }
        return activities;
    }

    private void bindActivityShot(ActivityEntity entity) throws ShotRemovedException {
        if (entity.getIdShot() != null) {
            Shot shot = ensureShotExistInLocal(entity);
            entity.setShotForMapping(shot);
        }
    }

    private Shot ensureShotExistInLocal(ActivityEntity activity) throws ShotRemovedException {
        String idShot = checkNotNull(activity.getIdShot());
        Shot localShot = localShotRepository.getShot(idShot);
        if (localShot != null) {
            return localShot;
        } else {
            Shot remoteShot = remoteShotRepository.getShot(idShot);
            localShotRepository.putShot(remoteShot);
            return checkNotNull(remoteShot,
              "Shot for activity not found remotely. Shot id: %s; Activity id: %s",
              idShot,
              activity.getIdActivity());
        }
    }
}
