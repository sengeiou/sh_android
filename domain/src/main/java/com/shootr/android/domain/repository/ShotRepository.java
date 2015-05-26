package com.shootr.android.domain.repository;

import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.EventTimelineParameters;
import java.util.List;

public interface ShotRepository {

    Shot putShot(Shot shot);

    List<Shot> getShotsForEventTimeline(EventTimelineParameters parameters);

    List<Shot> getShotsForActivityTimeline(ActivityTimelineParameters parameters);

    Shot getShot(String shotId);

    List<Shot> getReplies(String shot);
}
