package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Activity;
import java.util.List;

public interface ActivityRepository {

    List<Activity> getActivityTimeline(com.shootr.mobile.domain.ActivityTimelineParameters parameters);

    Activity getActivity(String activityId);

    void deleteActivitiesWithShot(String idShot);
}
