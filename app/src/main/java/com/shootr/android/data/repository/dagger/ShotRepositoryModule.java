package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.PreferencesSynchronizationRepository;
import com.shootr.android.data.repository.ShotQueueRepositoryImpl;
import com.shootr.android.data.repository.datasource.shot.DatabaseShotDataSource;
import com.shootr.android.data.repository.datasource.shot.ServiceShotDatasource;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.data.repository.local.LocalShotRepository;
import com.shootr.android.data.repository.remote.SyncShotRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.SynchronizationRepository;
import com.shootr.android.domain.service.ShotQueueRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    SyncShotRepository.class, LocalShotRepository.class, ServiceShotDatasource.class, DatabaseShotDataSource.class,
  },
  complete = false,
  library = true)
public class ShotRepositoryModule {

    @Provides @Singleton @Remote ShotRepository provideRemoteShotRepository(SyncShotRepository syncShotRepository) {
        return syncShotRepository;
    }

    @Provides @Singleton @Local ShotRepository provideLocalShotRepository(LocalShotRepository localShotRepository) {
        return localShotRepository;
    }

    @Provides @Singleton @Remote ShotDataSource provideRemoteShotDataSource(
      ServiceShotDatasource serviceShotDatasource) {
        return serviceShotDatasource;
    }

    @Provides @Singleton @Local ShotDataSource provideLocalShotDataSource(DatabaseShotDataSource databaseShotDataSource) {
        return databaseShotDataSource;
    }

    @Provides @Singleton ShotQueueRepository provideShotQueueRepository(ShotQueueRepositoryImpl shotQueueRepository) {
        return shotQueueRepository;
    }

    @Provides @Singleton SynchronizationRepository provideSynchronizationRepository(
      PreferencesSynchronizationRepository preferencesSynchronizationRepository) {
        return preferencesSynchronizationRepository;
    }
}
