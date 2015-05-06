package com.shootr.android.data.repository;

import com.shootr.android.domain.repository.EventListSynchronizationRepository;
import javax.inject.Inject;

public class MemoryEventListSynchronizationRepository implements EventListSynchronizationRepository {

    private static final long DEFAULT_REFRESH_DATE = 0L;

    long lastRefreshDate;

    @Inject public MemoryEventListSynchronizationRepository() {
        this.lastRefreshDate = DEFAULT_REFRESH_DATE;
    }

    @Override public Long getEventsRefreshDate() {
        return lastRefreshDate;
    }

    @Override public void setEventsRefreshDate(Long newRefreshDate) {
        lastRefreshDate = newRefreshDate;
    }
}
