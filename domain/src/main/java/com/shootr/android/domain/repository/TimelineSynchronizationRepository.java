package com.shootr.android.domain.repository;

public interface TimelineSynchronizationRepository {

    Long getStreamTimelineRefreshDate(String eventId);

    Long getActivityTimelineRefreshDate();

}
