package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.datasource.Cached;
import com.shootr.android.data.repository.datasource.watch.CachedWatchDatasource;
import com.shootr.android.data.repository.datasource.watch.DatabaseWatchDataSource;
import com.shootr.android.data.repository.datasource.watch.ServerWatchDataSource;
import com.shootr.android.data.repository.datasource.watch.WatchDataSource;
import com.shootr.android.data.repository.local.LocalWatchRepository;
import com.shootr.android.data.repository.remote.SyncWatchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.WatchRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    LocalWatchRepository.class, SyncWatchRepository.class, DatabaseWatchDataSource.class, ServerWatchDataSource.class,
  },
  complete = false,
  library = true)
public class WatchRepositoryModule {

    @Provides @Singleton @Local WatchRepository provideLocalWatchRepository(
      LocalWatchRepository watchRepository) {
        return watchRepository;
    }

    @Provides @Singleton @Remote WatchRepository provideRemoteWatchRepository(
      SyncWatchRepository watchRepository) {
        return watchRepository;
    }

    @Provides @Singleton @Local WatchDataSource provideLocalDataSource(
      DatabaseWatchDataSource watchDataSource) {
        return watchDataSource;
    }

    @Provides @Singleton @Remote WatchDataSource provideRemoteDataSource(
      ServerWatchDataSource watchDataSource) {
        return watchDataSource;
    }

    @Provides @Singleton @Cached WatchDataSource provideCachedDataSource(CachedWatchDatasource watchDatasource) {
        return watchDatasource;
    }
}
