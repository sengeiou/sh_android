package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.event.DatabaseFavoriteDataSource;
import com.shootr.mobile.data.repository.datasource.event.FavoriteDataSource;
import com.shootr.mobile.data.repository.datasource.event.ServiceFavoriteDataSource;
import com.shootr.mobile.data.repository.local.LocalFavoriteRepository;
import com.shootr.mobile.data.repository.remote.SyncFavoriteRepository;
import com.shootr.mobile.domain.repository.FavoriteRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    LocalFavoriteRepository.class, SyncFavoriteRepository.class
  },
  complete = false,
  library = true)
public class FavoriteRepositoryModule {

  @Provides @Singleton @Local
  FavoriteRepository provideLocalFavoriteRepository(LocalFavoriteRepository localFavoriteRepository) {
    return localFavoriteRepository;
  }

  @Provides @Singleton @Remote
  FavoriteRepository provideRemoteFavoriteRepository(SyncFavoriteRepository syncFavoriteRepository) {
    return syncFavoriteRepository;
  }

  @Provides @Singleton @Local
  FavoriteDataSource provideLocalFavoriteDataSource(DatabaseFavoriteDataSource databaseFavoriteDataSource) {
    return databaseFavoriteDataSource;
  }
  @Provides @Singleton @Remote
  FavoriteDataSource provideRemoteFavoriteDataSource(ServiceFavoriteDataSource serviceFavoriteDataSource) {
    return serviceFavoriteDataSource;
  }

}
