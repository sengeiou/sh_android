package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.stream.DatabaseRecentSearchDataSource;
import com.shootr.mobile.data.repository.datasource.stream.RecentSearchDataSource;
import com.shootr.mobile.data.repository.local.LocalRecentSearchRepository;
import com.shootr.mobile.domain.repository.stream.RecentSearchRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RecentSearchRepository.class
    },
    complete = false,
    library = true) public class RecentStreamRepositoryModule {

  @Provides @Singleton RecentSearchRepository provideRecentStreamRepository(
      LocalRecentSearchRepository recentStreamRepository) {
    return recentStreamRepository;
  }

  @Provides @Singleton RecentSearchDataSource provideRecentStreamDataSource(
      DatabaseRecentSearchDataSource recentStreamDataSource) {
    return recentStreamDataSource;
  }
}
