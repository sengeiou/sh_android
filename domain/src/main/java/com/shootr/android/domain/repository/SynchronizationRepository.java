package com.shootr.android.domain.repository;

public interface SynchronizationRepository {

    Long getTimelineLastRefresh();

    void putTimelineLastRefresh(Long date);
}
