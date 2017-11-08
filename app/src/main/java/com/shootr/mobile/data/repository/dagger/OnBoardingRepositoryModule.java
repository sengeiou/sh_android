package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.favorite.ExternalOnBoardingDatasource;
import com.shootr.mobile.data.repository.datasource.favorite.ServiceOnBoardingDataSource;
import com.shootr.mobile.data.repository.remote.SyncOnBoardingRepository;
import com.shootr.mobile.domain.repository.onBoarding.ExternalOnBoardingRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        SyncOnBoardingRepository.class
    },
    complete = false,
    library = true) public class OnBoardingRepositoryModule {

  @Provides @Singleton ExternalOnBoardingRepository provideExternalFavoriteRepository(
      SyncOnBoardingRepository syncFavoriteRepository) {
    return syncFavoriteRepository;
  }

  @Provides @Singleton ExternalOnBoardingDatasource provideRemoteFavoriteDataSource(
      ServiceOnBoardingDataSource serviceFavoriteDataSource) {
    return serviceFavoriteDataSource;
  }
}
