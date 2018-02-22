package com.shootr.mobile.domain.model;

public interface FeatureFlagType {

  String TIMELINE = "TIMELINE";
  String SOCKET_CONNECTION = "SOCKET_CONNECTION";

  String[] FOLLOWABLE_TYPES = {
      TIMELINE, SOCKET_CONNECTION
  };
}
