package com.shootr.mobile.data.repository;

import com.shootr.mobile.domain.repository.stream.StreamListSynchronizationRepository;
import javax.inject.Inject;

public class MemoryStreamListSynchronizationRepository implements StreamListSynchronizationRepository {

    public static final long DEFAULT_REFRESH_DATE = 0L;

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
