package com.shootr.mobile.ui.model;

public class UserSettingsModel {

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
}
