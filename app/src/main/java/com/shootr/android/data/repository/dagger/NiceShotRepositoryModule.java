package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.local.LocalNiceShotRepository;
import com.shootr.android.data.repository.remote.RemoteNiceShotRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.NiceShotRepository;
import com.shootr.android.domain.repository.Remote;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {

  },
  complete = false,
  library = true)
public class NiceShotRepositoryModule {

    @Provides
    @Local
    NiceShotRepository provideLocalNiceShotRepository(LocalNiceShotRepository localNiceShotRepository) {
        return localNiceShotRepository;
    }

    @Provides
    @Remote
    NiceShotRepository provideRemoteNiceShotRepository(RemoteNiceShotRepository remoteNiceShotRepository) {
        return remoteNiceShotRepository;
    }
}
