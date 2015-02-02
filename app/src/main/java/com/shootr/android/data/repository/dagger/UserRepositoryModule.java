package com.shootr.android.data.repository.dagger;

import com.shootr.android.data.repository.datasource.user.DatabaseUserDataSource;
import com.shootr.android.data.repository.datasource.user.ServiceUserDataSource;
import com.shootr.android.data.repository.datasource.user.UserDataSource;
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
    LocalUserRepository.class, SyncUserRepository.class, DatabaseUserDataSource.class, ServiceUserDataSource.class,
  },
  complete = false,
  library = true)
public class UserRepositoryModule {

    @Provides @Singleton @Local UserRepository provideLocalUserRepository(LocalUserRepository userRepository) {
        return userRepository;
    }

    @Provides @Singleton @Remote UserRepository provideRemoteUserRepository(SyncUserRepository userRepository) {
        return userRepository;
    }

    @Provides @Singleton @Local UserDataSource provideLocalUserDataSource(DatabaseUserDataSource userDataSource) {
        return userDataSource;
    }

    @Provides @Singleton @Remote UserDataSource provideRemoteUserDataSource(ServiceUserDataSource userDataSource) {
        return userDataSource;
    }
}