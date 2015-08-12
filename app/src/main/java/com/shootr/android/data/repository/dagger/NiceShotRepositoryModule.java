package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.local.LocalNiceShotRepository;
import com.shootr.android.domain.repository.NiceShotRepository;
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
    @Singleton
    NiceShotRepository provideNiceShotRepository(LocalNiceShotRepository localNiceShotRepository) {
        return localNiceShotRepository;
    }
}
