package com.shootr.android.data.repository;

import com.shootr.android.db.manager.ActivityManager;
import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.domain.repository.TimelineSynchronizationRepository;

import javax.inject.Inject;

public class DatabaseTimelineSynchronizationRepository implements TimelineSynchronizationRepository {

    private final ShotManager shotManager;
    private final ActivityManager activityManager;

    @Inject public DatabaseTimelineSynchronizationRepository(ShotManager shotManager, ActivityManager activityManager) {
        this.shotManager = shotManager;
        this.activityManager = activityManager;
    }

    @Override
    public Long getEventTimelineRefreshDate(String eventId) {
        return shotManager.getLastModifiedDateForEvent(eventId);
    }

    @Override
    public Long getActivityTimelineRefreshDate() {
        return activityManager.getLastModifiedDateForActivity();
    }
}
