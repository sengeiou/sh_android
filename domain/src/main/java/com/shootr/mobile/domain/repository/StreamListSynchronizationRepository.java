package com.shootr.mobile.domain.repository;

public interface StreamListSynchronizationRepository {

    Long getStreamsRefreshDate();

    void setStreamsRefreshDate(Long newRefreshDate);
}
