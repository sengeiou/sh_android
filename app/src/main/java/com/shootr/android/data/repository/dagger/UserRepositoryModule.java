package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.local.LocalUserRepository;
import com.shootr.android.data.repository.remote.SyncUserRepository;
import com.shootr.android.domain.repository.LocalRepository;
import com.shootr.android.domain.repository.RemoteRepository;
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

    @Provides @Singleton @LocalRepository UserRepository provideLocalUserRepository(
      LocalUserRepository userRepository) {
        return userRepository;
    }

    @Provides @Singleton @RemoteRepository UserRepository provideRemoteUserRepository(
      SyncUserRepository userRepository) {
        return userRepository;
    }
}
