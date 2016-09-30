package com.shootr.mobile.domain.repository.stream;

public interface StreamListSynchronizationRepository {

    Long getStreamsRefreshDate();

    void setStreamsRefreshDate(Long newRefreshDate);
}
