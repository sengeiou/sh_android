package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import java.util.List;

public interface ActivityRepository {

    List<Activity> getActivityTimeline(ActivityTimelineParameters parameters, String language);

    void deleteActivitiesWithShot(String idShot);

    void updateFollowStreamOnActivity(String idStream);

    void updateUnFollowStreamOnActivity(String idStream);
}
