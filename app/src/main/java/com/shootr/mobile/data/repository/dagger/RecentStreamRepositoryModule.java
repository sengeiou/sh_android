package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.stream.DatabaseRecentStreamDataSource;
import com.shootr.mobile.data.repository.datasource.stream.RecentStreamDataSource;
import com.shootr.mobile.data.repository.local.LocalRecentStreamRepository;
import com.shootr.mobile.domain.repository.RecentStreamRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RecentStreamRepository.class
    },
    complete = false,
    library = true) public class RecentStreamRepositoryModule {

  @Provides @Singleton RecentStreamRepository provideRecentStreamRepository(
      LocalRecentStreamRepository recentStreamRepository) {
    return recentStreamRepository;
  }

  @Provides @Singleton RecentStreamDataSource provideRecentStreamDataSource(
      DatabaseRecentStreamDataSource recentStreamDataSource) {
    return recentStreamDataSource;
  }
}
