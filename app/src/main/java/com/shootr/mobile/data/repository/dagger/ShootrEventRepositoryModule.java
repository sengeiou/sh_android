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
    library = true) public class ShootrEventRepositoryModule {

  @Provides @Singleton @Remote ShootrEventRepository provideRemoteShotEventRepository(
      RemoteShootrEventRepository remoteShootrEventRepository) {
    return remoteShootrEventRepository;
  }

  @Provides @Singleton @Remote ShootrEventDataSource provideServiceShotEventDatasource(
      ServiceShootrEventDatasource serviceShootrEventDatasource) {
    return serviceShootrEventDatasource;
  }

  @Provides @Singleton @Local ShootrEventRepository provideLocalShotEventRepository(
      LocalShootrEventRepository localShootrEventRepository) {
    return localShootrEventRepository;
  }

  @Provides @Singleton @Local ShootrEventDataSource provideLocalShotEventDataSource(
      DatabaseShootrEventDataSource databaseShootrEventDataSource) {
    return databaseShootrEventDataSource;
  }
}


