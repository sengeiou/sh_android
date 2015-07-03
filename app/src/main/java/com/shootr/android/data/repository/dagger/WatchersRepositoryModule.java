package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.local.LocalWatchersRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.WatchersRepository;
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
