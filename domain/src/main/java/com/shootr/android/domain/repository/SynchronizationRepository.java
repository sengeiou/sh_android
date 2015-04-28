package com.shootr.android.domain.repository;

public interface SynchronizationRepository {

    @Deprecated
    Long getTimelineLastRefresh();

    @Deprecated
    void putTimelineLastRefresh(Long date);

    Long getEventTimelineRefreshDate(Long eventId);

    Long getActivityTimelineRefreshDate();
}
