package com.shootr.mobile.data.repository.dagger;

import com.shootr.mobile.data.repository.datasource.settings.ExternalUserSettingsDatasource;
import com.shootr.mobile.data.repository.datasource.settings.ServiceUserSettingsDatasource;
import com.shootr.mobile.data.repository.remote.RemoteUserSettingsRepository;
import com.shootr.mobile.domain.repository.user.UserSettingsRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    injects = {
        RemoteUserSettingsRepository.class
    },
    complete = false,
    library = true) public class UserSettingsRepositoryModule {

  @Provides @Singleton UserSettingsRepository provideServerUserSettingsRepository(
      RemoteUserSettingsRepository remoteUserSettingsRepository) {
    return remoteUserSettingsRepository;
  }

  @Provides @Singleton ExternalUserSettingsDatasource provideServiceUserSettingsDatasource(
      ServiceUserSettingsDatasource serviceUserSettingsDatasource) {
    return serviceUserSettingsDatasource;
  }
}
