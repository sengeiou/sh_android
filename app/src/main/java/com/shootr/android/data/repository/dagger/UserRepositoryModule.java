package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.local.LocalUserRepository;
import com.shootr.android.data.repository.remote.SyncUserRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.UserRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
  injects = {
    LocalUserRepository.class, SyncUserRepository.class
  },
  complete = false,
  library = true)
public class UserRepositoryModule {

    @Provides @Singleton @Local UserRepository provideLocalUserRepository(
      LocalUserRepository userRepository) {
        return userRepository;
    }

    @Provides @Singleton @Remote UserRepository provideRemoteUserRepository(
      SyncUserRepository userRepository) {
        return userRepository;
    }
}
