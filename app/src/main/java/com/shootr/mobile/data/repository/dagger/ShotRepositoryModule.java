package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.DatabaseTimelineSynchronizationRepository;
import com.shootr.mobile.data.repository.ShotQueueRepositoryImpl;
import com.shootr.mobile.data.repository.datasource.shot.DatabaseShotDataSource;
import com.shootr.mobile.data.repository.datasource.shot.ServiceShotDatasource;
import com.shootr.mobile.data.repository.datasource.shot.ShotDataSource;
import com.shootr.mobile.data.repository.local.LocalShotRepository;
import com.shootr.mobile.data.repository.remote.SyncShotRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.TimelineSynchronizationRepository;
import com.shootr.mobile.domain.service.ShotQueueRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    @Provides @Singleton TimelineSynchronizationRepository provideTimelineSynchronizationRepository(
      DatabaseTimelineSynchronizationRepository databaseSynchronizationRepository) {
        return databaseSynchronizationRepository;
    }
}
