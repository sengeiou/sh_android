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
    switch (value.getPushType()) {
      case PushSettingType.STARTED_SHOOTING:
        userSettings.setStartedShootingPushSettings(value.getValue());
        break;
      case PushSettingType.NICE_SHOT:
        userSettings.setNiceShotPushSettings(value.getValue());
        break;
      case PushSettingType.SHARED_SHOT:
        userSettings.setReShotPushSettings(value.getValue());
        break;
      case PushSettingType.POLL:
        userSettings.setPollPushSettings(value.getValue());
        break;
      case PushSettingType.CHECK_IN:
        userSettings.setCheckinPushSettings(value.getValue());
        break;
      case PushSettingType.NEW_FOLLOWERS:
        userSettings.setNewFollowersPushSettings(value.getValue());
        break;
      case PushSettingType.PRIVATE_MESSAGE:
        userSettings.setPrivateMessagePushSettings(value.getValue());
        break;
      default:
        break;
    }
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

  public UserSettingsEntity mapNiceShot(UserSettings userSettings) {
    UserSettingsEntity response = new UserSettingsEntity();
    response.setPushType(PushSettingType.NICE_SHOT);
    response.setValue(userSettings.getNiceShotPushSettings());
    return response;
  }

  public UserSettingsEntity mapReShot(UserSettings userSettings) {
    UserSettingsEntity response = new UserSettingsEntity();
    response.setPushType(PushSettingType.SHARED_SHOT);
    response.setValue(userSettings.getReShotPushSettings());
    return response;
  }

  public UserSettingsEntity mapCheckin(UserSettings userSettings) {
    UserSettingsEntity response = new UserSettingsEntity();
    response.setPushType(PushSettingType.CHECK_IN);
    response.setValue(userSettings.getCheckinPushSettings());
    return response;
  }

  public UserSettingsEntity mapPrivateMessage(UserSettings userSettings) {
    UserSettingsEntity response = new UserSettingsEntity();
    response.setPushType(PushSettingType.PRIVATE_MESSAGE);
    response.setValue(userSettings.getPrivateMessagePushSettings());
    return response;
  }

  public UserSettingsEntity mapPoll(UserSettings userSettings) {
    UserSettingsEntity response = new UserSettingsEntity();
    response.setPushType(PushSettingType.POLL);
    response.setValue(userSettings.getPollPushSettings());
    return response;
  }

  public UserSettingsEntity mapNewFollowers(UserSettings userSettings) {
    UserSettingsEntity response = new UserSettingsEntity();
    response.setPushType(PushSettingType.NEW_FOLLOWERS);
    response.setValue(userSettings.getNewFollowersPushSettings());
    return response;
  }

  public UserSettings reverseMapList(List<UserSettingsEntity> userSettings) {
    UserSettings domainValue =
        UserSettings.builder().user(sessionRepository.getCurrentUserId()).build();
    if (userSettings != null) {
      for (UserSettingsEntity userSetting : userSettings) {
        handleType(domainValue, userSetting);
      }
    }
    return domainValue;
  }

  private void handleType(UserSettings domainValue, UserSettingsEntity userSetting) {
    switch (userSetting.getPushType()) {
      case PushSettingType.STARTED_SHOOTING:
        domainValue.setStartedShootingPushSettings(userSetting.getValue());
        break;
      case PushSettingType.NICE_SHOT:
        domainValue.setNiceShotPushSettings(userSetting.getValue());
        break;
      case PushSettingType.SHARED_SHOT:
        domainValue.setReShotPushSettings(userSetting.getValue());
        break;
      case PushSettingType.NEW_FOLLOWERS:
        domainValue.setNewFollowersPushSettings(userSetting.getValue());
        break;
      case PushSettingType.POLL:
        domainValue.setPollPushSettings(userSetting.getValue());
        break;
      case PushSettingType.CHECK_IN:
        domainValue.setCheckinPushSettings(userSetting.getValue());
        break;
      case PushSettingType.PRIVATE_MESSAGE:
        domainValue.setPrivateMessagePushSettings(userSetting.getValue());
        break;
      default:
        break;
    }
  }
}
