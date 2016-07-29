package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.discovered.ExternalDiscoveredDatasource;
import com.shootr.mobile.data.repository.datasource.discovered.ServiceDiscoveredDatasource;
import com.shootr.mobile.data.repository.local.LocalDiscoveredRepository;
import com.shootr.mobile.data.repository.remote.SyncDiscoveredRepository;
import com.shootr.mobile.domain.repository.discover.ExternalDiscoveredRepository;
import com.shootr.mobile.domain.repository.discover.InternalDiscoveredRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        LocalDiscoveredRepository.class, SyncDiscoveredRepository.class,
        ServiceDiscoveredDatasource.class
    },
    complete = false,
    library = true)public class DiscoveredRepositoryModule {

  @Provides @Singleton InternalDiscoveredRepository provideInternalDiscoverRepository(
      LocalDiscoveredRepository localDiscoverRepository) {
    return localDiscoverRepository;
  }

  @Provides @Singleton ExternalDiscoveredRepository provideExternalDiscoverRepository(
      SyncDiscoveredRepository syncDiscoveredRepository) {
    return syncDiscoveredRepository;
  }

  @Provides @Singleton ExternalDiscoveredDatasource provideExternalDiscoveredDatasource(
      ServiceDiscoveredDatasource serviceDiscoveredDatasource) {
    return serviceDiscoveredDatasource;
  }

}
