package com.shootr.android.domain.repository;

public interface EventListSynchronizationRepository {

    Long getEventsRefreshDate();

    void setEventsRefreshDate(Long newRefreshDate);

}
