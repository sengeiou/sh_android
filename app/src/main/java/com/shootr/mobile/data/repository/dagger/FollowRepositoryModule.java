package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.user.DatabaseFollowDataSource;
import com.shootr.mobile.data.repository.datasource.user.FollowDataSource;
import com.shootr.mobile.data.repository.datasource.user.ServiceFollowDataSource;
import com.shootr.mobile.data.repository.local.LocalFollowRepository;
import com.shootr.mobile.data.repository.remote.SyncFollowRepository;
import com.shootr.mobile.domain.repository.FollowRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {

  },
  complete = false,
  library = true) public class FollowRepositoryModule {

    @Provides
    @Singleton
    @Local
    FollowRepository provideLocalFollowRepository(LocalFollowRepository localFollowRepository) {
        return localFollowRepository;
    }

    @Provides
    @Singleton
    @Remote
    FollowRepository provideRemoteFollowRepository(SyncFollowRepository remoteFollowRepository) {
        return remoteFollowRepository;
    }

    @Provides
    @Singleton
    @Local FollowDataSource provideLocalFollowDataSource(DatabaseFollowDataSource databaseFollowDataSource) {
        return databaseFollowDataSource;
    }

    @Provides
    @Singleton
    @Remote
    FollowDataSource provideRemoteFollowDataSource(ServiceFollowDataSource serviceFollowDataSource) {
        return serviceFollowDataSource;
    }
}
