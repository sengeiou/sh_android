package com.shootr.android.domain.repository;

public interface TimelineSynchronizationRepository {

    Long getEventTimelineRefreshDate(Long eventId);

    Long getActivityTimelineRefreshDate();

}
