package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.ShotQueueRepositoryImpl;
import com.shootr.android.data.repository.datasource.shot.ServiceShotDatasource;
import com.shootr.android.data.repository.datasource.shot.ShotDataSource;
import com.shootr.android.data.repository.remote.SyncShotRepository;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.service.ShotQueueRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    SyncShotRepository.class, ServiceShotDatasource.class,
  },
  complete = false,
  library = true)
public class ShotRepositoryModule {

    @Provides @Singleton @Remote ShotRepository provideRemoteShotRepository(SyncShotRepository syncShotRepository) {
        return syncShotRepository;
    }

    @Provides @Singleton @Remote ShotDataSource provideRemoteShotDataSource(
      ServiceShotDatasource serviceShotDatasource) {
        return serviceShotDatasource;
    }

    @Provides @Singleton ShotQueueRepository provideShotQueueRepository(ShotQueueRepositoryImpl shotQueueRepository) {
        return shotQueueRepository;
    }
}
