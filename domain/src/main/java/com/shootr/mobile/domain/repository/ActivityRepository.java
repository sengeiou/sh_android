package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import java.util.List;

public interface ActivityRepository {

    List<Activity> getActivityTimeline(ActivityTimelineParameters parameters, String language);

    Activity getActivity(String activityId);

    void deleteActivitiesWithShot(String idShot);
}
