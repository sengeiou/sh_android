package com.shootr.android.data.repository.sync;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncableRepositoriesProvider {

    private final List<SyncableRepository> syncableRepositories;

    @Inject public SyncableRepositoriesProvider() {
        syncableRepositories = new ArrayList<>();
    }

    public List<SyncableRepository> getSyncableRepositories() {
        return syncableRepositories;
    }
}
