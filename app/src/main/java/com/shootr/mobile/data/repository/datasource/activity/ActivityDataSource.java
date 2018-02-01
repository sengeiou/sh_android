package com.shootr.mobile.data.repository.datasource.activity;

import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import java.util.List;

public interface ActivityDataSource {

    List<ActivityEntity> getActivityTimeline(ActivityTimelineParameters parameters, String language);

    ActivityEntity getActivity(String activityId);

    void putActivities(ActivityTimelineParameters parameters, List<ActivityEntity> activityEntities);

    void deleteActivitiesWithShot(String idShot);

    void updateFollowStreamOnActivity(String idStream);

    void updateUnFollowStreamOnActivity(String idStream);
}
