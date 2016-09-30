package com.shootr.mobile.data.repository.remote;

import android.support.annotation.Nullable;
import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.data.mapper.ActivityEntityMapper;
import com.shootr.mobile.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class SyncActivityRepository implements ActivityRepository {

  private final ActivityDataSource localActivityDataSource;
  private final ActivityDataSource remoteActivityDataSource;
  private final ActivityEntityMapper activityEntityMapper;
  private final ExternalShotRepository remoteShotRepository;
  private final InternalShotRepository localShotRepository;

  @Inject public SyncActivityRepository(@Local ActivityDataSource localActivityDataSource,
      @Remote ActivityDataSource remoteActivityDataSource,
      ActivityEntityMapper activityEntityMapper, ExternalShotRepository remoteShotRepository,
      InternalShotRepository localShotRepository) {
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
    localActivityDataSource.putActivities(parameters, activities);
    return activityEntityMapper.transform(activities);
  }

  @Override public void deleteActivitiesWithShot(String idShot) {
    throw new IllegalArgumentException("not implemented");
  }

  private List<ActivityEntity> bindActivityShots(List<ActivityEntity> activityEntities,
      ActivityTimelineParameters parameters) {
    List<ActivityEntity> activities = new ArrayList<>();
    for (ActivityEntity entity : activityEntities) {
      if (bindActivityShot(entity, parameters)) {
        activities.add(entity);
      }
    }
    return activities;
  }

  private boolean bindActivityShot(ActivityEntity entity, ActivityTimelineParameters parameters) {
    if (entity.getIdShot() != null) {
      Shot shot = ensureShotExistInLocal(entity, parameters);
      if (shot != null) {
        entity.setShotForMapping(shot);
        return true;
      }
      return false;
    }
    return true;
  }

  private Shot ensureShotExistInLocal(ActivityEntity activity,
      ActivityTimelineParameters parameters) {
    String idShot = checkNotNull(activity.getIdShot());
    Shot localShot =
        localShotRepository.getShot(idShot, parameters.getStreamTypes(), parameters.getShotTypes());
    if (localShot != null) {
      return localShot;
    } else {
      return getRemoteShot(activity, parameters, idShot);
    }
  }

  @Nullable
  private Shot getRemoteShot(ActivityEntity activity, ActivityTimelineParameters parameters,
      String idShot) {
    try {
      Shot remoteShot = remoteShotRepository.getShot(idShot, parameters.getStreamTypes(),
          parameters.getShotTypes());
      localShotRepository.putShot(remoteShot);
      return checkNotNull(remoteShot,
          "Shot for activity not found remotely. Shot id: %s; Activity id: %s", idShot,
          activity.getIdActivity());
    } catch (ShotNotFoundException exception) {
      return null;
    }
  }
}
