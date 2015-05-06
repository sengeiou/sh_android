package com.shootr.android.domain.repository;

public interface TimelineSynchronizationRepository {

    Long getEventTimelineRefreshDate(String eventId);

    Long getActivityTimelineRefreshDate();

}
