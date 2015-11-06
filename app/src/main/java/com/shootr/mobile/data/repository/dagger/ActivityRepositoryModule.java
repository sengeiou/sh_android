package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.activity.ActivityDataSource;
import com.shootr.mobile.data.repository.datasource.activity.DatabaseActivityDataSource;
import com.shootr.mobile.data.repository.datasource.activity.ServiceActivityDataSource;
import com.shootr.mobile.data.repository.local.LocalActivityRepository;
import com.shootr.mobile.data.repository.remote.SyncActivityRepository;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    SyncActivityRepository.class,
    LocalActivityRepository.class,
    ServiceActivityDataSource.class,
    DatabaseActivityDataSource.class,
  },
  complete = false,
  library = true)
public class ActivityRepositoryModule {
    @Provides @Singleton @Remote ActivityRepository provideRemoteActivityRepository(SyncActivityRepository syncActivityRepository) {
        return syncActivityRepository;
    }

    @Provides @Singleton @Local ActivityRepository provideLocalActivityRepository(LocalActivityRepository localActivityRepository) {
        return localActivityRepository;
    }

    @Provides @Singleton @Remote ActivityDataSource provideRemoteActivityDataSource(
      ServiceActivityDataSource serviceActivityDataSource) {
        return serviceActivityDataSource;
    }

    @Provides @Singleton @Local ActivityDataSource provideLocalActivityDataSource(DatabaseActivityDataSource databaseActivityDataSource) {
        return databaseActivityDataSource;
    }
}
