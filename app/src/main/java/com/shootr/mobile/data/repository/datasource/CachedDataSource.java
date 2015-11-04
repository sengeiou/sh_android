package com.shootr.mobile.data.repository.datasource;

public interface CachedDataSource {

    boolean isValid();

    void invalidate();
}
