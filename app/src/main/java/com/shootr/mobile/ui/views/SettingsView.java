package com.shootr.mobile.ui.views;

public interface SettingsView {

  void showError(String messageForError);

  void setStartedShootingSettings(Integer startedShootingPushSettings);

  void setNiceShotSettings(Integer startedShootingPushSettings);

  void setReShotSettings(Integer reShotPushSettings);

  void sendNotificationAnalytics(String type);
}
