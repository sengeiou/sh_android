package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.local.LocalWatchersRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.WatchersRepository;
import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
        LocalWatchersRepository.class
  },
  complete = false,
  library = true)
public class WatchersRepositoryModule {

    @Provides @Local
    WatchersRepository provideLocalWatchersRepository(LocalWatchersRepository watchersRepository) {
        return watchersRepository;
    }
}
