package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.ServiceSocketDataSource;
import com.shootr.mobile.data.repository.datasource.SocketDataSource;
import com.shootr.mobile.data.repository.remote.RemoteSocketRepository;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RemoteSocketRepository.class, ServiceSocketDataSource.class,
    },
    complete = false,
    library = true) public class SocketRepositoryModule {

  @Provides @Singleton @Remote SocketRepository provideSocketRepository(
      RemoteSocketRepository remoteSocketRepository) {
    return remoteSocketRepository;
  }

  @Provides @Singleton @Remote SocketDataSource provideRemoteSocketDataSource(
      ServiceSocketDataSource serviceSocketDataSource) {
    return serviceSocketDataSource;
  }
}
