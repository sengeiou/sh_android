package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.contributor.ContributorDataSource;
import com.shootr.mobile.data.repository.datasource.contributor.ServiceContributorDataSource;
import com.shootr.mobile.data.repository.remote.SyncContributorRepository;
import com.shootr.mobile.domain.repository.ContributorRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    SyncContributorRepository.class, ServiceContributorDataSource.class
  },
  complete = false,
  library = true) public class ContributorRepositoryModule {

    @Provides @Singleton ContributorRepository provideContributorRepository(SyncContributorRepository contributorRepository) {
        return contributorRepository;
    }

    @Provides @Singleton ContributorDataSource provideContributorDataSource(ServiceContributorDataSource serviceContributorDataSource) {
        return serviceContributorDataSource;
    }
}