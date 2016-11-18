package com.shootr.mobile.ui.model;

public class UserSettingsModel {

  private String idUser;
  private String startedShootingPushSettings;
  private String niceShotPushSettings;
  private String reShotPushSettings;
  private String newFollowersPushSettings;
  private String pollPushSettings;
  private String checkinPushSettings;

  public String getNewFollowersPushSettings() {
    return newFollowersPushSettings;
  }

  public void setNewFollowersPushSettings(String newFollowersPushSettings) {
    this.newFollowersPushSettings = newFollowersPushSettings;
  }

  public String getPollPushSettings() {
    return pollPushSettings;
  }

  public void setPollPushSettings(String pollPushSettings) {
    this.pollPushSettings = pollPushSettings;
  }

  public String getCheckinPushSettings() {
    return checkinPushSettings;
  }

  public void setCheckinPushSettings(String checkinPushSettings) {
    this.checkinPushSettings = checkinPushSettings;
  }

  public String getReShotPushSettings() {
    return reShotPushSettings;
  }

  public void setReShotPushSettings(String reShotPushSettings) {
    this.reShotPushSettings = reShotPushSettings;
  }

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
