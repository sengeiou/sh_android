package com.shootr.android.data.repository.sync;

import com.shootr.android.data.repository.remote.SyncFavoriteRepository;
import com.shootr.android.data.repository.remote.SyncUserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncableRepositoriesProvider {

    private final List<SyncableRepository> syncableRepositories;

    @Inject public SyncableRepositoriesProvider(SyncUserRepository syncUserRepository, SyncFavoriteRepository syncFavoriteRepository) {
        syncableRepositories = new ArrayList<>();
        syncableRepositories.add(syncUserRepository);
        syncableRepositories.add(syncFavoriteRepository);
    }

    public List<SyncableRepository> getSyncableRepositories() {
        return syncableRepositories;
    }
}
