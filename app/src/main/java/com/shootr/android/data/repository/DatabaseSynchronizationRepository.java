package com.shootr.android.data.repository;

import com.shootr.android.db.manager.ShotManager;
import com.shootr.android.domain.repository.SynchronizationRepository;

import javax.inject.Inject;

public class DatabaseSynchronizationRepository implements SynchronizationRepository {

    private final ShotManager shotManager;

    @Inject public DatabaseSynchronizationRepository(ShotManager shotManager) {
        this.shotManager = shotManager;
    }

    @Override
    public Long getEventTimelineRefreshDate(Long eventId) {
        return shotManager.getLastModifiedDateForEvent(eventId);
    }

    @Override
    public Long getActivityTimelineRefreshDate() {
        return shotManager.getLastModifiedDateForActivity();
    }
}
