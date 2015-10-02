package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.datasource.user.DatabaseFollowDataSource;
import com.shootr.android.data.repository.datasource.user.FollowDataSource;
import com.shootr.android.data.repository.datasource.user.ServiceFollowDataSource;
import com.shootr.android.data.repository.local.LocalFollowRepository;
import com.shootr.android.data.repository.remote.SyncFollowRepository;
import com.shootr.android.domain.repository.FollowRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
    @Local
    FollowDataSource provideLocalFollowDataSource(DatabaseFollowDataSource databaseFollowDataSource) {
        return databaseFollowDataSource;
    }

    @Provides
    @Singleton
    @Remote
    FollowDataSource provideRemoteFollowDataSource(ServiceFollowDataSource serviceFollowDataSource) {
        return serviceFollowDataSource;
    }
}
