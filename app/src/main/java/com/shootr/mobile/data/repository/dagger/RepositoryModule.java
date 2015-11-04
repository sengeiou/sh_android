package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.sync.SyncDispatcher;
import com.shootr.mobile.data.repository.sync.SyncDispatcherImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  includes = {
    UserRepositoryModule.class,
    StreamRepositoryModule.class,
    ShotRepositoryModule.class,
    WatchersRepositoryModule.class,
    FavoriteRepositoryModule.class,
    ActivityRepositoryModule.class,
    DatabaseUtilsModule.class,
    NiceShotRepositoryModule.class,
    DeviceRepositoryModule.class,
    FollowRepositoryModule.class,
  },
  complete = false,
  library = true
)
public class RepositoryModule {

    @Provides @Singleton SyncDispatcher provideSyncDispatcher(SyncDispatcherImpl syncDispatcher) {
        return syncDispatcher;
    }
}