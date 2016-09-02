package com.shootr.mobile.domain.model.user;

import com.shootr.mobile.domain.model.PushSettingType;

public class UserSettings {

  private String idUser;
  private String startedShootingPushSettings;
  private String niceShotPushSettings;

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String getStartedShootingPushSettings() {
    return startedShootingPushSettings;
  }

  public void setStartedShootingPushSettings(String startedShootingPushSettings) {
    this.startedShootingPushSettings = startedShootingPushSettings;
  }

  public String getNiceShotPushSettings() {
    return niceShotPushSettings;
  }

  public void setNiceShotPushSettings(String niceShotPushSettings) {
    this.niceShotPushSettings = niceShotPushSettings;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private UserSettings settings = new UserSettings();

    public Builder() {
      setDefaults();
    }

    private void setDefaults() {
      settings.startedShootingPushSettings = PushSettingType.STARTED_SHOOTING_ALL;
      settings.niceShotPushSettings = PushSettingType.NICE_SHOT_ALL;
    }

    public Builder user(String idUser) {
      settings.idUser = idUser;
      return this;
    }

    public Builder startedShootingPushSettings(String value) {
      settings.startedShootingPushSettings = value;
      return this;
    }

    public UserSettings build() {
      return settings;
    }
  }
}
