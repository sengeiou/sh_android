package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.ServiceTimelineDataSource;
import com.shootr.mobile.data.repository.datasource.StreamTimelineDataSource;
import com.shootr.mobile.data.repository.local.LocalTimelineRepository;
import com.shootr.mobile.data.repository.remote.RemoteTimelineRepository;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.timeline.ExternalTimelineRepository;
import com.shootr.mobile.domain.repository.timeline.InternalTimelineRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
      RemoteTimelineRepository.class, LocalTimelineRepository.class
    },
    complete = false,
    library = true) public class TimelineRepositoryModule {

  @Provides @Singleton @Remote StreamTimelineDataSource provideRemoteStreamTimelineDataSource(
      ServiceTimelineDataSource serviceTimelineDataSource) {
    return serviceTimelineDataSource;
  }

  @Provides @Singleton ExternalTimelineRepository provideRemoteTimelineRepository(
      RemoteTimelineRepository remoteTimelineRepository) {
    return remoteTimelineRepository;
  }

  @Provides @Singleton InternalTimelineRepository provideLocalTimelineRepository(
      LocalTimelineRepository localTimelineRepository) {
    return localTimelineRepository;
  }
}

