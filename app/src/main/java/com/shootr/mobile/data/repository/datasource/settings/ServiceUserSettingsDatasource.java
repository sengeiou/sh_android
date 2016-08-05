package com.shootr.mobile.data.repository.datasource.settings;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.UserSettingsApiService;
import com.shootr.mobile.data.entity.UserSettingsEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceUserSettingsDatasource implements ExternalUserSettingsDatasource {

  private final UserSettingsApiService userSettingsApiService;

  @Inject public ServiceUserSettingsDatasource(UserSettingsApiService userSettingsApiService) {
    this.userSettingsApiService = userSettingsApiService;
  }

  @Override public List<UserSettingsEntity> getUserSettings() {
    try {
      List<UserSettingsEntity> userSettings = userSettingsApiService.getUserSettings();
      if (userSettings.size() == 0) {
        return null;
      } else {
        return userSettings;
      }
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void modifyPushSettings(UserSettingsEntity userSettingsEntity) {
    try {
      userSettingsApiService.setUserSettings(userSettingsEntity);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }
}
