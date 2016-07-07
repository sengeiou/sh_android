package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.poll.DatabasePollDataSource;
import com.shootr.mobile.data.repository.datasource.poll.PollDataSource;
import com.shootr.mobile.data.repository.datasource.poll.ServicePollDataSource;
import com.shootr.mobile.data.repository.local.LocalPollRepository;
import com.shootr.mobile.data.repository.remote.RemotePollRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.PollRepository;
import com.shootr.mobile.domain.repository.Remote;
import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
        LocalPollRepository.class,
        RemotePollRepository.class
    },
    complete = false,
    library = true) public class PollRepositoryModule {

  @Provides @Local PollRepository provideLocalPollRepository(LocalPollRepository pollRepository) {
    return pollRepository;
  }

  @Provides @Remote PollRepository provideRemotePollRepository(RemotePollRepository pollRepository) {
    return pollRepository;
  }

  @Provides @Local PollDataSource provideLocalPollDataSource(DatabasePollDataSource pollDataSource) {
    return pollDataSource;
  }

  @Provides @Remote PollDataSource provideRemotePollDataSource(ServicePollDataSource pollDataSource) {
    return pollDataSource;
  }
}
