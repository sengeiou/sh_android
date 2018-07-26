package com.shootr.mobile.domain.model;

public interface FeatureFlagType {

  String TIMELINE = "TIMELINE";
  String SOCKET_CONNECTION = "SOCKET_CONNECTION";
  String SHOT_DETAIL_CONNECTION = "SHOT_DETAIL";
  String SUPER_SHOT = "SUPERSHOT";

  String[] FOLLOWABLE_TYPES = {
      TIMELINE, SOCKET_CONNECTION, SHOT_DETAIL_CONNECTION
  };
}
