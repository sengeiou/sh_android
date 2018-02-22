package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.log.DatabaseLogDataSource;
import com.shootr.mobile.data.repository.datasource.log.LogDataSource;
import com.shootr.mobile.data.repository.datasource.log.ServiceLogDatasource;
import com.shootr.mobile.data.repository.local.LocalLogRepository;
import com.shootr.mobile.data.repository.remote.RemoteLogRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.log.LogRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RemoteLogRepository.class, ServiceLogDatasource.class,
        LocalLogRepository.class, DatabaseLogDataSource.class,
    },
    complete = false,
    library = true) public class LogRepositoryModule {

  @Provides @Singleton @Remote LogRepository provideRemoteLogRepository(
      RemoteLogRepository remoteLogRepository) {
    return remoteLogRepository;
  }

  @Provides @Singleton @Remote LogDataSource provideServiceLogDatasource(
      ServiceLogDatasource serviceLogDatasource) {
    return serviceLogDatasource;
  }

  @Provides @Singleton @Local LogRepository provideLocalLogRepository(
      LocalLogRepository localLogRepository) {
    return localLogRepository;
  }

  @Provides @Singleton @Local LogDataSource provideLocalLogDataSource(
      DatabaseLogDataSource databaseLogDataSource) {
    return databaseLogDataSource;
  }
}


