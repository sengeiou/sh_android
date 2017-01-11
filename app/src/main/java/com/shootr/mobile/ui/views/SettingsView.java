package com.shootr.mobile.ui.views;

public interface SettingsView {

  void showError(String messageForError);

  void setStartedShootingSettings(Integer startedShootingPushSettings);

  void setNiceShotSettings(Integer startedShootingPushSettings);

  void setReShotSettings(Integer reShotPushSettings);

  void setPollSettings(Integer reShotPushSettings);

  void setPrivateMessageSettings(Integer reShotPushSettings);

  void setCheckinSettings(Integer reShotPushSettings);

  void setNewFollowersSettings(Integer reShotPushSettings);

  void sendNotificationAnalytics(String type);
}
