package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.MemoryStreamListSynchronizationRepository;
import com.shootr.mobile.data.repository.datasource.stream.DatabaseMemoryStreamSearchDataSource;
import com.shootr.mobile.data.repository.datasource.stream.DatabaseStreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.ServiceStreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.ServiceStreamListDataSource;
import com.shootr.mobile.data.repository.datasource.stream.ServiceStreamSearchDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamListDataSource;
import com.shootr.mobile.data.repository.datasource.stream.StreamSearchDataSource;
import com.shootr.mobile.data.repository.local.LocalStreamRepository;
import com.shootr.mobile.data.repository.local.LocalStreamSearchRepository;
import com.shootr.mobile.data.repository.remote.RemoteStreamSearchRepository;
import com.shootr.mobile.data.repository.remote.SyncStreamRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.StreamListSynchronizationRepository;
import com.shootr.mobile.domain.repository.StreamRepository;
import com.shootr.mobile.domain.repository.StreamSearchRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    LocalStreamRepository.class, SyncStreamRepository.class
  },
  complete = false,
  library = true) public class StreamRepositoryModule {

    @Provides @Singleton @Local StreamRepository provideLocalStreamRepository(LocalStreamRepository streamRepository) {
        return streamRepository;
    }

    @Provides @Singleton @Remote StreamRepository provideRemoteStreamRepository(SyncStreamRepository streamRepository) {
        return streamRepository;
    }

    @Provides @Singleton @Local StreamDataSource provideLocalStreamDataSource(
      DatabaseStreamDataSource streamDataSource) {
        return streamDataSource;
    }

    @Provides @Singleton @Remote StreamDataSource provideRemoteStreamDataSource(
      ServiceStreamDataSource streamDataSource) {
        return streamDataSource;
    }

    @Provides @Local StreamSearchRepository provideLocalStreamSearchRepository(
      LocalStreamSearchRepository streamSearchRepository) {
        return streamSearchRepository;
    }

    @Provides @Remote StreamSearchRepository provideLocalStreamSearchRepository(
      RemoteStreamSearchRepository streamSearchRepository) {
        return streamSearchRepository;
    }

    @Provides @Remote StreamListDataSource provideRemoteStreamListDataSource(
      ServiceStreamListDataSource serviceStreamListDataSource) {
        return serviceStreamListDataSource;
    }

    @Provides @Remote StreamSearchDataSource provideRemoteStreamSearchDataSource(
      ServiceStreamSearchDataSource serviceStreamSearchDataSource) {
        return serviceStreamSearchDataSource;
    }

    @Provides @Local StreamSearchDataSource provideLocalStreamSearchDataSource(
      DatabaseMemoryStreamSearchDataSource serviceStreamSearchDataSource) {
        return serviceStreamSearchDataSource;
    }

    @Provides @Singleton StreamListSynchronizationRepository provideStreamListSynchronizationRepository(
      MemoryStreamListSynchronizationRepository memoryStreamListSynchronizationRepository) {
        return memoryStreamListSynchronizationRepository;
    }
}
