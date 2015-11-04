package com.shootr.mobile.data.repository;

import com.shootr.mobile.db.manager.ActivityManager;
import com.shootr.mobile.db.manager.StreamManager;
import com.shootr.mobile.domain.repository.TimelineSynchronizationRepository;
import javax.inject.Inject;

public class DatabaseTimelineSynchronizationRepository implements TimelineSynchronizationRepository {

    private final ActivityManager activityManager;
    private final StreamManager streamManager;

    @Inject public DatabaseTimelineSynchronizationRepository(ActivityManager activityManager,
      StreamManager streamManager) {
        this.activityManager = activityManager;
        this.streamManager = streamManager;
    }

    @Override
    public Long getStreamTimelineRefreshDate(String streamId) {
        return streamManager.getLastModifiedDateForStream(streamId);
    }

    @Override
    public void setStreamTimelineRefreshDate(String streamId, Long refreshDate) {
        streamManager.setLastModifiedDateForStream(streamId, refreshDate);
    }

    @Override
    public Long getActivityTimelineRefreshDate() {
        return activityManager.getLastModifiedDateForActivity();
    }
}
