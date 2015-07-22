package com.shootr.android.data.repository;

import com.shootr.android.domain.repository.StreamListSynchronizationRepository;
import javax.inject.Inject;

public class MemoryStreamListSynchronizationRepository implements StreamListSynchronizationRepository {

    private static final long DEFAULT_REFRESH_DATE = 0L;

    long lastRefreshDate;

    @Inject public MemoryStreamListSynchronizationRepository() {
        this.lastRefreshDate = DEFAULT_REFRESH_DATE;
    }

    @Override public Long getStreamsRefreshDate() {
        return lastRefreshDate;
    }

    @Override public void setStreamsRefreshDate(Long newRefreshDate) {
        lastRefreshDate = newRefreshDate;
    }
}
