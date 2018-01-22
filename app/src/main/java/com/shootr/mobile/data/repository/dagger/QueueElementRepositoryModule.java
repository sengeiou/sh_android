package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.QueueElementDataSource;
import com.shootr.mobile.data.repository.datasource.ServiceQueueElementDatasource;
import com.shootr.mobile.data.repository.remote.RemoteQueueElementRepository;
import com.shootr.mobile.domain.repository.QueueElementRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
      RemoteQueueElementRepository.class, ServiceQueueElementDatasource.class
  },
  complete = false,
  library = true) public class QueueElementRepositoryModule {

  @Provides @Singleton QueueElementDataSource provideQueueElementDataSource(
      ServiceQueueElementDatasource serviceQueueElementDatasource) {
    return serviceQueueElementDatasource;
  }

    @Provides @Singleton QueueElementRepository provideQueueElementRepository(
      RemoteQueueElementRepository remoteQueueElementRepository) {
        return remoteQueueElementRepository;
    }
}
