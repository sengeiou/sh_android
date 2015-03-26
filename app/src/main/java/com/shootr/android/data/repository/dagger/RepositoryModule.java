package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.sync.SyncDispatcher;
import com.shootr.android.data.repository.sync.SyncDispatcherImpl;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  includes = {
    UserRepositoryModule.class, EventRepositoryModule.class, ShotRepositoryModule.class,
  },
  complete = false,
  library = true
)
public class RepositoryModule {

    @Provides @Singleton SyncDispatcher provideSyncDispatcher(SyncDispatcherImpl syncDispatcher) {
        return syncDispatcher;
    }
}
