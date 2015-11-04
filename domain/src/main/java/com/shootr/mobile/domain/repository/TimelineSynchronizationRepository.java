package com.shootr.mobile.domain.repository;

public interface TimelineSynchronizationRepository {

    Long getStreamTimelineRefreshDate(String streamId);

    void setStreamTimelineRefreshDate(String streamId, Long refreshDate);

    Long getActivityTimelineRefreshDate();

}
