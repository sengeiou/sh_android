package com.shootr.mobile.data.repository.datasource.activity;

import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import java.util.List;

public interface ActivityDataSource {

    List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters, String language);

    ActivityEntity getActivity(String activityId);

    void putActivities(List<ActivityEntity> activityEntities);

    void deleteActivitiesWithShot(String idShot);
}
