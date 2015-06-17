package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.datasource.event.DatabaseFavoriteDataSource;
import com.shootr.android.data.repository.datasource.event.FavoriteDataSource;
import com.shootr.android.data.repository.datasource.event.ServiceFavoriteDataSource;
import com.shootr.android.data.repository.local.LocalFavoriteRepository;
import com.shootr.android.data.repository.remote.SyncFavoriteRepository;
import com.shootr.android.domain.repository.FavoriteRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
