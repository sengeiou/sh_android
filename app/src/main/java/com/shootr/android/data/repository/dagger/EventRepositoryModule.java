package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.MemoryEventListSynchronizationRepository;
import com.shootr.android.data.repository.datasource.event.DatabaseEventDataSource;
import com.shootr.android.data.repository.datasource.event.DatabaseMemoryEventSearchDataSource;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceEventDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceEventSearchDataSource;
import com.shootr.android.data.repository.local.LocalEventRepository;
import com.shootr.android.data.repository.local.LocalEventSearchRepository;
import com.shootr.android.data.repository.remote.RemoteEventSearchRepository;
import com.shootr.android.data.repository.remote.SyncEventRepository;
import com.shootr.android.domain.repository.EventListSynchronizationRepository;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    LocalEventRepository.class, SyncEventRepository.class
  },
  complete = false,
  library = true)
public class EventRepositoryModule {

    @Provides @Singleton @Local EventRepository provideLocalEventRepository(
      LocalEventRepository eventRepository) {
        return eventRepository;
    }

    @Provides @Singleton @Remote EventRepository provideRemoteEventRepository(
      SyncEventRepository eventRepository) {
        return eventRepository;
    }

    @Provides @Singleton @Local EventDataSource provideLocalEventDataSource(
      DatabaseEventDataSource eventDataSource) {
        return eventDataSource;
    }

    @Provides @Singleton @Remote EventDataSource provideRemoteEventDataSource(
      ServiceEventDataSource eventDataSource) {
        return eventDataSource;
    }

    @Provides @Local EventSearchRepository provideLocalEventSearchRepository(LocalEventSearchRepository eventSearchRepository) {
        return eventSearchRepository;
    }

    @Provides @Remote EventSearchRepository provideLocalEventSearchRepository(RemoteEventSearchRepository eventSearchRepository) {
        return eventSearchRepository;
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
