package com.shootr.mobile.domain.repository.user;

import com.shootr.mobile.domain.model.user.UserSettings;

public interface UserSettingsRepository {

  UserSettings getUserSettings();

  void modifyUserSettings(UserSettings userSettings, String pushType);

}