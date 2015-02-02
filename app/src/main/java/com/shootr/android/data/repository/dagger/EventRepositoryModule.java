package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.datasource.event.DatabaseEventDataSource;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceEventDataSource;
import com.shootr.android.data.repository.local.LocalEventRepository;
import com.shootr.android.data.repository.remote.SyncEventRepository;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
}