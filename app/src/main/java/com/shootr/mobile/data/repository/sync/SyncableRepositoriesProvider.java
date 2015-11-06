package com.shootr.mobile.data.repository.sync;

import com.shootr.mobile.data.repository.remote.SyncFavoriteRepository;
import com.shootr.mobile.data.repository.remote.SyncFollowRepository;
import com.shootr.mobile.data.repository.remote.SyncUserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SyncableRepositoriesProvider {

    private final List<SyncableRepository> syncableRepositories;

    @Inject
    public SyncableRepositoriesProvider(SyncUserRepository syncUserRepository,
      SyncFavoriteRepository syncFavoriteRepository,
      SyncFollowRepository syncFollowRepository) {
        syncableRepositories = new ArrayList<>();
        syncableRepositories.add(syncUserRepository);
        syncableRepositories.add(syncFavoriteRepository);
        syncableRepositories.add(syncFollowRepository);
    }

    public List<SyncableRepository> getSyncableRepositories() {
        return syncableRepositories;
    }
}
