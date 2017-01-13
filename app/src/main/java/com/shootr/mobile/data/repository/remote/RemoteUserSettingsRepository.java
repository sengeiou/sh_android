package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.UserSettingsEntity;
import com.shootr.mobile.data.mapper.UserSettingsMapper;
import com.shootr.mobile.data.repository.datasource.settings.ExternalUserSettingsDatasource;
import com.shootr.mobile.domain.model.PushSettingType;
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

  @Override public void modifyUserSettings(UserSettings userSettings, String pushType) {
    UserSettingsEntity userSettingsEntities;
    switch (pushType) {
      case PushSettingType.STARTED_SHOOTING:
        userSettingsEntities = mapper.mapStartedShooting(userSettings);
        break;
      case PushSettingType.NICE_SHOT:
        userSettingsEntities = mapper.mapNiceShot(userSettings);
        break;
      case PushSettingType.SHARED_SHOT:
        userSettingsEntities = mapper.mapReShot(userSettings);
        break;
      case PushSettingType.NEW_FOLLOWERS:
        userSettingsEntities = mapper.mapNewFollowers(userSettings);
        break;
      case PushSettingType.POLL:
        userSettingsEntities = mapper.mapPoll(userSettings);
        break;
      case PushSettingType.CHECK_IN:
        userSettingsEntities = mapper.mapCheckin(userSettings);
        break;
      case PushSettingType.PRIVATE_MESSAGE:
        userSettingsEntities = mapper.mapPrivateMessage(userSettings);
        break;
      default:
        userSettingsEntities = new UserSettingsEntity();
        break;
    }
    datasource.modifyPushSettings(userSettingsEntities);
  }
}
