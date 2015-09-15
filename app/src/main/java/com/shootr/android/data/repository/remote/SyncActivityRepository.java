package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.mapper.ActivityEntityMapper;
import com.shootr.android.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
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
        bindActivityShots(activityEntities);
        localActivityDataSource.putActivities(activityEntities);
        return activityEntityMapper.transform(activityEntities);
    }

    @Override
    public Activity getActivity(String activityId) {
        ActivityEntity activity = remoteActivityDataSource.getActivity(activityId);
        bindActivityShot(activity);
        return activityEntityMapper.transform(activity);
    }

    @Override public void deleteActivitiesWithShot(String idShot) {
        throw new IllegalArgumentException("not implemented");
    }

    private void bindActivityShots(List<ActivityEntity> activityEntities) {
        for (ActivityEntity entity : activityEntities) {
            bindActivityShot(entity);
        }
    }

    private void bindActivityShot(ActivityEntity entity) {
        if (entity.getIdShot() != null) {
            Shot shot = ensureShotExistInLocal(entity);
            entity.setShotForMapping(shot);
        }
    }

    private Shot ensureShotExistInLocal(ActivityEntity activity) {
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
