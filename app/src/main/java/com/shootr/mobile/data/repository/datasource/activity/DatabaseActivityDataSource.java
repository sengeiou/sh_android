package com.shootr.mobile.data.repository.datasource.activity;

import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.db.manager.ActivityManager;
import com.shootr.mobile.db.manager.MeActivityManager;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import java.util.List;
import javax.inject.Inject;

public class DatabaseActivityDataSource implements ActivityDataSource {

  private final ActivityManager activityManager;
  private final MeActivityManager meActivityManager;

  @Inject public DatabaseActivityDataSource(ActivityManager activityManager,
      MeActivityManager meActivityManager) {
    this.activityManager = activityManager;
    this.meActivityManager = meActivityManager;
  }

  @Override public List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters,
      String language) {
    if (parameters.isMeTimeline()) {
      return meActivityManager.getActivityTimelineFromParameters(parameters);
    } else {
      return activityManager.getActivityTimelineFromParameters(parameters);
    }
  }

  @Override public ActivityEntity getActivity(String activityId) {
    return activityManager.getActivity(activityId);
  }

  @Override public void putActivities(ActivityTimelineParameters parameters,
      List<ActivityEntity> activityEntities) {
    if (parameters.isMeTimeline()) {
      meActivityManager.saveActivities(activityEntities);
    } else {
      activityManager.saveActivities(activityEntities);
    }
  }

  @Override public void deleteActivitiesWithShot(String idShot) {
    activityManager.deleteActivitiesWithShot(idShot);
  }

  @Override public void updateFollowStreamOnActivity(String idStream) {
    activityManager.updateFollowStream(idStream);
    meActivityManager.updateFollowStream(idStream);
  }

  @Override public void updateUnFollowStreamOnActivity(String idStream) {
    activityManager.updateUnFollowStream(idStream);
    meActivityManager.updateUnFollowStream(idStream);
  }
}
