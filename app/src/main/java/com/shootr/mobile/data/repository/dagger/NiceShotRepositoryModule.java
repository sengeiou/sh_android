package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.local.LocalNiceShotRepository;
import com.shootr.mobile.data.repository.remote.RemoteNiceShotRepository;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.nice.InternalNiceShotRepository;
import com.shootr.mobile.domain.repository.nice.NiceShotRepository;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {

  },
  complete = false,
  library = true) public class NiceShotRepositoryModule {

    @Provides InternalNiceShotRepository provideLocalNiceShotRepository(
      LocalNiceShotRepository localNiceShotRepository) {
        return localNiceShotRepository;
    }

    @Provides @Remote NiceShotRepository provideRemoteNiceShotRepository(
      RemoteNiceShotRepository remoteNiceShotRepository) {
        return remoteNiceShotRepository;
    }
}
