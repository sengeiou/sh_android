package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.user.UserSettings;
import com.shootr.mobile.ui.model.UserSettingsModel;
import com.shootr.mobile.infraestructure.Mapper;
import javax.inject.Inject;

public class UserSettingsModelMapper extends Mapper<UserSettings, UserSettingsModel> {

  @Inject public UserSettingsModelMapper() {
  }

  @Override public UserSettingsModel map(UserSettings value) {
    UserSettingsModel model = new UserSettingsModel();
    model.setIdUser(value.getIdUser());
    model.setStartedShootingPushSettings(value.getStartedShootingPushSettings());
    return model;
  }

  @Override public UserSettings reverseMap(UserSettingsModel value) {
    UserSettings domainEntity = new UserSettings();
    domainEntity.setStartedShootingPushSettings(value.getStartedShootingPushSettings());
    domainEntity.setIdUser(value.getIdUser());
    return domainEntity;
  }
}
