package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.shot.DatabaseShotEventDataSource;
import com.shootr.mobile.data.repository.datasource.shot.ServiceShotEventDatasource;
import com.shootr.mobile.data.repository.datasource.shot.ShotEventDataSource;
import com.shootr.mobile.data.repository.local.LocalShotEventRepository;
import com.shootr.mobile.data.repository.remote.RemoteShotEventRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.shot.ShotEventRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RemoteShotEventRepository.class, ServiceShotEventDatasource.class,
        LocalShotEventRepository.class, DatabaseShotEventDataSource.class,
    },
    complete = false,
    library = true) public class ShotEventRepositoryModule {

  @Provides @Singleton @Remote ShotEventRepository provideRemoteShotEventRepository(
      RemoteShotEventRepository remoteShotEventRepository) {
    return remoteShotEventRepository;
  }

  @Provides @Singleton @Remote ShotEventDataSource provideServiceShotEventDatasource(
      ServiceShotEventDatasource serviceShotEventDatasource) {
    return serviceShotEventDatasource;
  }

  @Provides @Singleton @Local ShotEventRepository provideLocalShotEventRepository(
      LocalShotEventRepository localShotEventRepository) {
    return localShotEventRepository;
  }

  @Provides @Singleton @Local ShotEventDataSource provideLocalShotEventDataSource(
      DatabaseShotEventDataSource databaseShotEventDataSource) {
    return databaseShotEventDataSource;
  }
}


