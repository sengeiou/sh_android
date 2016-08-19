package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.UserSettingsEntity;
import com.shootr.mobile.domain.model.PushSettingType;
import com.shootr.mobile.domain.model.user.UserSettings;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.infraestructure.Mapper;
import java.util.List;
import javax.inject.Inject;

public class UserSettingsMapper extends Mapper<UserSettingsEntity, UserSettings> {

  private final SessionRepository sessionRepository;

  @Inject public UserSettingsMapper(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override public UserSettings map(UserSettingsEntity value) {
    UserSettings userSettings = new UserSettings();
    userSettings.setIdUser(sessionRepository.getCurrentUserId());
    userSettings.setStartedShootingPushSettings(value.getValue());
    return userSettings;
  }

  @Override public UserSettingsEntity reverseMap(UserSettings value) {
    UserSettingsEntity userSettingsEntity = new UserSettingsEntity();
    userSettingsEntity.setPushType(PushSettingType.STARTED_SHOOTING);
    userSettingsEntity.setValue(value.getStartedShootingPushSettings());
    return userSettingsEntity;
  }

  public UserSettingsEntity mapStartedShooting(UserSettings userSettings) {
    UserSettingsEntity response = new UserSettingsEntity();
    response.setPushType(PushSettingType.STARTED_SHOOTING);
    response.setValue(userSettings.getStartedShootingPushSettings());
    return response;
  }

  public UserSettings reverseMapList(List<UserSettingsEntity> userSettings) {
    UserSettings domainValue =
        UserSettings.builder().user(sessionRepository.getCurrentUserId()).build();
    if (userSettings != null) {
      for (UserSettingsEntity userSetting : userSettings) {
        if (userSetting.getPushType().equals(PushSettingType.STARTED_SHOOTING)) {
          domainValue.setStartedShootingPushSettings(userSetting.getValue());
        }
      }
    }
    return domainValue;
  }
}
