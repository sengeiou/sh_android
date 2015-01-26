package com.shootr.android.data.repository.sync;

import com.shootr.android.data.repository.remote.SyncWatchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncableRepositoriesProvider {

    private final List<SyncableRepository> syncableRepositories;

    @Inject public SyncableRepositoriesProvider(SyncWatchRepository watchRepository) {
        syncableRepositories = new ArrayList<>();
        syncableRepositories.add(watchRepository);
    }

    public List<SyncableRepository> getSyncableRepositories() {
        return syncableRepositories;
    }
}
