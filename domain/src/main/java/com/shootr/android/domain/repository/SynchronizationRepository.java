package com.shootr.android.domain.repository;

public interface SynchronizationRepository {

    Long getEventTimelineRefreshDate(Long eventId);

    Long getActivityTimelineRefreshDate();
}
