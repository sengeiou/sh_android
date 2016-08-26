package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.UserSettingsEntity;
import com.shootr.mobile.data.mapper.UserSettingsMapper;
import com.shootr.mobile.data.repository.datasource.settings.ExternalUserSettingsDatasource;
import com.shootr.mobile.domain.model.user.UserSettings;
import com.shootr.mobile.domain.repository.user.UserSettingsRepository;
import javax.inject.Inject;

public class RemoteUserSettingsRepository implements UserSettingsRepository {

  private final ExternalUserSettingsDatasource datasource;
  private final UserSettingsMapper mapper;

  @Inject public RemoteUserSettingsRepository(ExternalUserSettingsDatasource datasource,
      UserSettingsMapper mapper) {
    this.datasource = datasource;
    this.mapper = mapper;
  }

  @Override public UserSettings getUserSettings() {
    return mapper.reverseMapList(datasource.getUserSettings());
  }

  @Override public void modifyStartedShootingSettings(UserSettings userSettings) {
    UserSettingsEntity userSettingsEntities = mapper.mapStartedShooting(userSettings);
    datasource.modifyPushSettings(userSettingsEntities);
  }

  @Override public void modifyNiceShotSettings(UserSettings userSettings) {
    UserSettingsEntity userSettingsEntities = mapper.mapNiceShot(userSettings);
    datasource.modifyPushSettings(userSettingsEntities);
  }
}
