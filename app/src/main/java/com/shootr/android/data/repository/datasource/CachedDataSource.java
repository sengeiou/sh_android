package com.shootr.android.data.repository.datasource;

public interface CachedDataSource {

    boolean isValid();

    void invalidate();
}
