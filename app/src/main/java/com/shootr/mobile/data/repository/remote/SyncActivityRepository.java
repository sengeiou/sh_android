package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.data.mapper.ActivityEntityMapper;
import com.shootr.mobile.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class SyncActivityRepository implements ActivityRepository {

  private final ActivityDataSource localActivityDataSource;
  private final ActivityDataSource remoteActivityDataSource;
  private final ActivityEntityMapper activityEntityMapper;
  private final ShotRepository remoteShotRepository;
  private final ShotRepository localShotRepository;

  @Inject public SyncActivityRepository(@Local ActivityDataSource localActivityDataSource,
      @Remote ActivityDataSource remoteActivityDataSource,
      ActivityEntityMapper activityEntityMapper, @Remote ShotRepository remoteShotRepository,
      @Local ShotRepository localShotRepository) {
    this.localActivityDataSource = localActivityDataSource;
    this.remoteActivityDataSource = remoteActivityDataSource;
    this.activityEntityMapper = activityEntityMapper;
    this.remoteShotRepository = remoteShotRepository;
    this.localShotRepository = localShotRepository;
  }

  @Override
  public List<Activity> getActivityTimeline(ActivityTimelineParameters parameters, String locale) {
    List<ActivityEntity> activityEntities =
        remoteActivityDataSource.getActivityTimeline(parameters, locale);
    List<ActivityEntity> activities = bindActivityShots(activityEntities, parameters);
    localActivityDataSource.putActivities(activities);
    return activityEntityMapper.transform(activities);
  }

  @Override public void deleteActivitiesWithShot(String idShot) {
    throw new IllegalArgumentException("not implemented");
  }

  private List<ActivityEntity> bindActivityShots(List<ActivityEntity> activityEntities,
      ActivityTimelineParameters parameters) {
    List<ActivityEntity> activities = new ArrayList<>();
    for (ActivityEntity entity : activityEntities) {
      bindActivityShot(entity, parameters);
      activities.add(entity);
    }
    return activities;
  }

  private void bindActivityShot(ActivityEntity entity, ActivityTimelineParameters parameters) {
    if (entity.getIdShot() != null) {
      Shot shot = ensureShotExistInLocal(entity, parameters);
      entity.setShotForMapping(shot);
    }
  }

  private Shot ensureShotExistInLocal(ActivityEntity activity,
      ActivityTimelineParameters parameters) {
    String idShot = checkNotNull(activity.getIdShot());
    Shot localShot =
        localShotRepository.getShot(idShot, parameters.getStreamTypes(), parameters.getShotTypes());
    if (localShot != null) {
      return localShot;
    } else {
      Shot remoteShot = remoteShotRepository.getShot(idShot, parameters.getStreamTypes(),
          parameters.getShotTypes());
      localShotRepository.putShot(remoteShot);
      return checkNotNull(remoteShot,
          "Shot for activity not found remotely. Shot id: %s; Activity id: %s", idShot,
          activity.getIdActivity());
    }
  }
}
