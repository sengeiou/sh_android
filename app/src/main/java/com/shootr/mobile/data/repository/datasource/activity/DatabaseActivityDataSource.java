package com.shootr.mobile.data.repository.datasource.activity;

import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.db.manager.ActivityManager;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import java.util.List;
import javax.inject.Inject;

public class DatabaseActivityDataSource implements ActivityDataSource {

    private final ActivityManager activityManager;

    @Inject public DatabaseActivityDataSource(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    @Override public List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters, String language) {
        return activityManager.getActivityTimelineFromParameters(parameters);
    }

    @Override
    public ActivityEntity getActivity(String activityId) {
        return activityManager.getActivity(activityId);
    }

    @Override public void putActivities(List<ActivityEntity> activityEntities) {
        activityManager.saveActivities(activityEntities);
    }

    @Override public void deleteActivitiesWithShot(String idShot) {
        activityManager.deleteActivitiesWithShot(idShot);
    }
}
