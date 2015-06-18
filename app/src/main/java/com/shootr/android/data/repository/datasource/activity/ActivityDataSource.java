package com.shootr.android.data.repository.datasource.activity;

import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.domain.ActivityTimelineParameters;
import java.util.List;

public interface ActivityDataSource {

    List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters);

    void putActivities(List<ActivityEntity> activityEntities);
}
