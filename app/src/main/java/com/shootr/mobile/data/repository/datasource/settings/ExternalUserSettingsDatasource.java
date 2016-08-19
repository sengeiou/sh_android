package com.shootr.mobile.data.repository.datasource.settings;

import com.shootr.mobile.data.entity.UserSettingsEntity;
import java.util.List;

public interface ExternalUserSettingsDatasource {

  List<UserSettingsEntity> getUserSettings();

  void modifyPushSettings(UserSettingsEntity userSettingsEntity);
}
