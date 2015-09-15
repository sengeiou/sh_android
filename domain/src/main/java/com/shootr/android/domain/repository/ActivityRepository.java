package com.shootr.android.domain.repository;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimelineParameters;
import java.util.List;

public interface ActivityRepository {

    List<Activity> getActivityTimeline(ActivityTimelineParameters parameters);

    Activity getActivity(String activityId);

    void deleteActivitiesWithShot(String idShot);
}
