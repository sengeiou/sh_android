package com.shootr.android.data.repository.datasource.activity;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.db.manager.ActivityManager;
import com.shootr.android.domain.ActivityTimelineParameters;
import java.util.List;
import javax.inject.Inject;

public class DatabaseActivityDataSource implements ActivityDataSource {

    private final ActivityManager activityManager;

    @Inject public DatabaseActivityDataSource(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    @Override public List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters,
      String currentUserId) {
        return activityManager.getActivityTimelineFromParameters(parameters);
    }

    @Override public void putActivities(List<ActivityEntity> activityEntities) {
        activityManager.saveActivities(activityEntities);
    }
}
