package com.shootr.android.domain.repository;

public interface StreamListSynchronizationRepository {

    Long getStreamsRefreshDate();

    void setStreamsRefreshDate(Long newRefreshDate);

}
