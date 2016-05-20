package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.contributor.ContributorDataSource;
import com.shootr.mobile.data.repository.datasource.contributor.DatabaseContributorDatasource;
import com.shootr.mobile.data.repository.datasource.contributor.ServiceContributorDataSource;
import com.shootr.mobile.data.repository.local.LocalContributorRepository;
import com.shootr.mobile.data.repository.remote.SyncContributorRepository;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        SyncContributorRepository.class, ServiceContributorDataSource.class,
        LocalContributorRepository.class, DatabaseContributorDatasource.class,
    },
    complete = false,
    library = true) public class ContributorRepositoryModule {

  @Provides @Singleton @Remote ContributorRepository provideRemoteContributorRepository(
      SyncContributorRepository contributorRepository) {
    return contributorRepository;
  }

  @Provides @Singleton @Local ContributorRepository provideLocalContributorRepository(
      LocalContributorRepository localContributorRepository) {
    return localContributorRepository;
  }

  @Provides @Singleton @Remote ContributorDataSource provideRemoteContributorDataSource(
      ServiceContributorDataSource serviceContributorDataSource) {
    return serviceContributorDataSource;
  }

  @Provides @Singleton @Local ContributorDataSource provideLocalContributorDataSource(
      DatabaseContributorDatasource databaseContributorDatasource) {
    return databaseContributorDatasource;
  }
}