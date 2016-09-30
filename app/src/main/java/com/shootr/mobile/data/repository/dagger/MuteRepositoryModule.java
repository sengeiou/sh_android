package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.stream.DatabaseMuteDataSource;
import com.shootr.mobile.data.repository.datasource.stream.MuteDataSource;
import com.shootr.mobile.data.repository.datasource.stream.ServiceMuteDataSource;
import com.shootr.mobile.data.repository.local.LocalMuteRepository;
import com.shootr.mobile.data.repository.remote.SyncMuteRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.stream.MuteRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    LocalMuteRepository.class, SyncMuteRepository.class, DatabaseMuteDataSource.class, ServiceMuteDataSource.class,
  },
  complete = false,
  library = true) public class MuteRepositoryModule {

    @Provides @Singleton @Local MuteRepository provideLocalMuteRepository(LocalMuteRepository localMuteRepository) {
        return localMuteRepository;
    }

    @Provides @Singleton @Remote MuteRepository provideRemoteMuteRepository(SyncMuteRepository remoteMuteRepository) {
        return remoteMuteRepository;
    }

    @Provides @Singleton @Local MuteDataSource provideLocalMuteDataSource(
      DatabaseMuteDataSource databaseMuteDataSource) {
        return databaseMuteDataSource;
    }

    @Provides @Singleton @Remote MuteDataSource provideRemoteMuteDataSource(
      ServiceMuteDataSource serviceMuteDataSource) {
        return serviceMuteDataSource;
    }
}
