package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.favorite.DatabaseFavoriteDatasource;
import com.shootr.mobile.data.repository.datasource.favorite.ExternalFavoriteDatasource;
import com.shootr.mobile.data.repository.datasource.favorite.InternalFavoriteDatasource;
import com.shootr.mobile.data.repository.datasource.favorite.ServiceFavoriteDataSource;
import com.shootr.mobile.data.repository.local.LocalFavoriteRepository;
import com.shootr.mobile.data.repository.remote.SyncFavoriteRepository;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        LocalFavoriteRepository.class, SyncFavoriteRepository.class
    },
    complete = false,
    library = true) public class FavoriteRepositoryModule {

  @Provides @Singleton InternalFavoriteRepository provideInternalFavoriteRepository(
      LocalFavoriteRepository localFavoriteRepository) {
    return localFavoriteRepository;
  }

  @Provides @Singleton ExternalFavoriteRepository provideExternalFavoriteRepository(
      SyncFavoriteRepository syncFavoriteRepository) {
    return syncFavoriteRepository;
  }

  @Provides @Singleton InternalFavoriteDatasource provideLocalFavoriteDataSource(
      DatabaseFavoriteDatasource databaseFavoriteDataSource) {
    return databaseFavoriteDataSource;
  }

  @Provides @Singleton ExternalFavoriteDatasource provideRemoteFavoriteDataSource(
      ServiceFavoriteDataSource serviceFavoriteDataSource) {
    return serviceFavoriteDataSource;
  }
}
