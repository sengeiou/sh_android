package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.user.UserSettings;
import com.shootr.mobile.infraestructure.Mapper;
import com.shootr.mobile.ui.model.UserSettingsModel;
import javax.inject.Inject;

public class UserSettingsModelMapper extends Mapper<UserSettings, UserSettingsModel> {

  @Inject public UserSettingsModelMapper() {
  }

  @Override public UserSettingsModel map(UserSettings value) {
    UserSettingsModel model = new UserSettingsModel();
    model.setIdUser(value.getIdUser());
    model.setStartedShootingPushSettings(value.getStartedShootingPushSettings());
    model.setNiceShotPushSettings(value.getNiceShotPushSettings());
    model.setReShotPushSettings(value.getReShotPushSettings());
    model.setNewFollowersPushSettings(value.getNewFollowersPushSettings());
    model.setPollPushSettings(value.getPollPushSettings());
    model.setCheckinPushSettings(value.getCheckinPushSettings());
    return model;
  }

  @Override public UserSettings reverseMap(UserSettingsModel value) {
    UserSettings domainEntity = new UserSettings();
    domainEntity.setStartedShootingPushSettings(value.getStartedShootingPushSettings());
    domainEntity.setIdUser(value.getIdUser());
    domainEntity.setNiceShotPushSettings(value.getNiceShotPushSettings());
    domainEntity.setReShotPushSettings(value.getReShotPushSettings());
    domainEntity.setNewFollowersPushSettings(value.getNewFollowersPushSettings());
    domainEntity.setPollPushSettings(value.getPollPushSettings());
    domainEntity.setCheckinPushSettings(value.getCheckinPushSettings());
    return domainEntity;
  }
}
