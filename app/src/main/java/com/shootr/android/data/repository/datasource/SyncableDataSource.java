package com.shootr.android.data.repository.datasource;

import java.util.List;

public interface SyncableDataSource<T> {

    List<T> getEntitiesNotSynchronized();
}
