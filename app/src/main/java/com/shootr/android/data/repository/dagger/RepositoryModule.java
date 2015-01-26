package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.datasource.DatabaseWatchDataSource;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.RemoteDataSource;
import com.shootr.android.data.repository.datasource.ServerWatchDataSource;
import com.shootr.android.data.repository.datasource.WatchDataSource;
import com.shootr.android.data.repository.local.LocalWatchRepository;
import com.shootr.android.data.repository.remote.SyncWatchRepository;
import com.shootr.android.data.repository.sync.SyncDispatcher;
import com.shootr.android.data.repository.sync.SyncDispatcherImpl;
import com.shootr.android.domain.repository.LocalRepository;
import com.shootr.android.domain.repository.RemoteRepository;
import com.shootr.android.domain.repository.WatchRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    LocalWatchRepository.class, SyncWatchRepository.class, DatabaseWatchDataSource.class, ServerWatchDataSource.class,
  },
  complete = false,
  library = true
)
public class RepositoryModule {

    @Provides @Singleton SyncDispatcher provideSyncDispatcher(SyncDispatcherImpl syncDispatcher) {
        return syncDispatcher;
    }

    @Provides @Singleton @LocalRepository WatchRepository provideLocalWatchRepository(
      LocalWatchRepository watchRepository) {
        return watchRepository;
    }

    @Provides @Singleton @RemoteRepository WatchRepository provideRemoteWatchRepository(
      SyncWatchRepository watchRepository) {
        return watchRepository;
    }

    @Provides @Singleton @LocalDataSource WatchDataSource provideLocalDataSource(
      DatabaseWatchDataSource watchDataSource) {
        return watchDataSource;
    }

    @Provides @Singleton @RemoteDataSource WatchDataSource provideRemoteDataSource(
      ServerWatchDataSource watchDataSource) {
        return watchDataSource;
    }
}
