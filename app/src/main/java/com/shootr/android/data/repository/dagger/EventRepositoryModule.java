package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.MemoryEventListSynchronizationRepository;
import com.shootr.android.data.repository.datasource.event.DatabaseStreamDataSource;
import com.shootr.android.data.repository.datasource.event.DatabaseMemoryEventSearchDataSource;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.datasource.event.EventListDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceStreamDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceEventListDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceEventSearchDataSource;
import com.shootr.android.data.repository.local.LocalStreamRepository;
import com.shootr.android.data.repository.local.LocalEventSearchRepository;
import com.shootr.android.data.repository.remote.RemoteEventSearchRepository;
import com.shootr.android.data.repository.remote.SyncStreamRepository;
import com.shootr.android.domain.repository.EventListSynchronizationRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    LocalStreamRepository.class, SyncStreamRepository.class
  },
  complete = false,
  library = true)
public class EventRepositoryModule {

    @Provides @Singleton @Local StreamRepository provideLocalEventRepository(
      LocalStreamRepository eventRepository) {
        return eventRepository;
    }

    @Provides @Singleton @Remote StreamRepository provideRemoteEventRepository(
      SyncStreamRepository eventRepository) {
        return eventRepository;
    }

    @Provides @Singleton @Local StreamDataSource provideLocalEventDataSource(
      DatabaseStreamDataSource eventDataSource) {
        return eventDataSource;
    }

    @Provides @Singleton @Remote StreamDataSource provideRemoteEventDataSource(
      ServiceStreamDataSource eventDataSource) {
        return eventDataSource;
    }

    @Provides @Local EventSearchRepository provideLocalEventSearchRepository(LocalEventSearchRepository eventSearchRepository) {
        return eventSearchRepository;
    }

    @Provides @Remote EventSearchRepository provideLocalEventSearchRepository(RemoteEventSearchRepository eventSearchRepository) {
        return eventSearchRepository;
    }

    @Provides @Remote EventListDataSource provideRemoteEventListDataSource(ServiceEventListDataSource serviceEventListDataSource) {
        return serviceEventListDataSource;
    }

    @Provides @Remote EventSearchDataSource provideRemoteEventSearchDataSource(
      ServiceEventSearchDataSource serviceEventSearchDataSource) {
        return serviceEventSearchDataSource;
    }

    @Provides @Local EventSearchDataSource provideLocalEventSearchDataSource(
      DatabaseMemoryEventSearchDataSource serviceEventSearchDataSource) {
        return serviceEventSearchDataSource;
    }

    @Provides @Singleton EventListSynchronizationRepository provideEventListSynchronizationRepository(
      MemoryEventListSynchronizationRepository memoryEventListSynchronizationRepository) {
        return memoryEventListSynchronizationRepository;
    }
}
