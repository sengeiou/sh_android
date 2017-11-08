package com.shootr.mobile.data.repository.sync;

import com.shootr.mobile.data.repository.remote.SyncOnBoardingRepository;
import com.shootr.mobile.data.repository.remote.SyncFollowRepository;
import com.shootr.mobile.data.repository.remote.SyncShotRepository;
import com.shootr.mobile.data.repository.remote.SyncUserRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SyncableRepositoriesProvider {

    private final List<SyncableRepository> syncableRepositories;

    @Inject public SyncableRepositoriesProvider(SyncUserRepository syncUserRepository,
      SyncOnBoardingRepository syncFavoriteRepository, SyncFollowRepository syncFollowRepository,
      SyncShotRepository syncShotRepository) {
        syncableRepositories = new ArrayList<>();
        syncableRepositories.add(syncUserRepository);
        syncableRepositories.add(syncFavoriteRepository);
        syncableRepositories.add(syncFollowRepository);
        syncableRepositories.add(syncShotRepository);
    }

    public List<SyncableRepository> getSyncableRepositories() {
        return syncableRepositories;
    }
}
