package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.MemoryEventListSynchronizationRepository;
import com.shootr.android.data.repository.datasource.event.DatabaseStreamDataSource;
import com.shootr.android.data.repository.datasource.event.DatabaseMemoryStreamSearchDataSource;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.datasource.event.StreamListDataSource;
import com.shootr.android.data.repository.datasource.event.StreamSearchDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceStreamDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceStreamListDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceStreamSearchDataSource;
import com.shootr.android.data.repository.local.LocalStreamRepository;
import com.shootr.android.data.repository.local.LocalStreamSearchRepository;
import com.shootr.android.data.repository.remote.RemoteStreamSearchRepository;
import com.shootr.android.data.repository.remote.SyncStreamRepository;
import com.shootr.android.domain.repository.EventListSynchronizationRepository;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.StreamSearchRepository;
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

    @Provides @Local
    StreamSearchRepository provideLocalEventSearchRepository(LocalStreamSearchRepository eventSearchRepository) {
        return eventSearchRepository;
    }

    @Provides @Remote
    StreamSearchRepository provideLocalEventSearchRepository(RemoteStreamSearchRepository eventSearchRepository) {
        return eventSearchRepository;
    }

    @Provides @Remote
    StreamListDataSource provideRemoteEventListDataSource(ServiceStreamListDataSource serviceEventListDataSource) {
        return serviceEventListDataSource;
    }

    @Provides @Remote StreamSearchDataSource provideRemoteEventSearchDataSource(
      ServiceStreamSearchDataSource serviceEventSearchDataSource) {
        return serviceEventSearchDataSource;
    }

    @Provides @Local StreamSearchDataSource provideLocalEventSearchDataSource(
      DatabaseMemoryStreamSearchDataSource serviceEventSearchDataSource) {
        return serviceEventSearchDataSource;
    }

    @Provides @Singleton EventListSynchronizationRepository provideEventListSynchronizationRepository(
      MemoryEventListSynchronizationRepository memoryEventListSynchronizationRepository) {
        return memoryEventListSynchronizationRepository;
    }
}
