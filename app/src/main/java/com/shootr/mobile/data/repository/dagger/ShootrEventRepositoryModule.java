package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.shot.DatabaseShootrEventDataSource;
import com.shootr.mobile.data.repository.datasource.shot.ServiceShootrEventDatasource;
import com.shootr.mobile.data.repository.datasource.shot.ShootrEventDataSource;
import com.shootr.mobile.data.repository.local.LocalShootrEventRepository;
import com.shootr.mobile.data.repository.remote.RemoteShootrEventRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.shot.ShootrEventRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RemoteShootrEventRepository.class, ServiceShootrEventDatasource.class,
        LocalShootrEventRepository.class, DatabaseShootrEventDataSource.class,
    },
    complete = false,
    library = true) public class ShotEventRepositoryModule {

  @Provides @Singleton @Remote ShootrEventRepository provideRemoteShotEventRepository(
      RemoteShootrEventRepository remoteShotEventRepository) {
    return remoteShotEventRepository;
  }

  @Provides @Singleton @Remote ShootrEventDataSource provideServiceShotEventDatasource(
      ServiceShootrEventDatasource serviceShotEventDatasource) {
    return serviceShotEventDatasource;
  }

  @Provides @Singleton @Local ShootrEventRepository provideLocalShotEventRepository(
      LocalShootrEventRepository localShotEventRepository) {
    return localShotEventRepository;
  }

  @Provides @Singleton @Local ShootrEventDataSource provideLocalShotEventDataSource(
      DatabaseShootrEventDataSource databaseShotEventDataSource) {
    return databaseShotEventDataSource;
  }
}


