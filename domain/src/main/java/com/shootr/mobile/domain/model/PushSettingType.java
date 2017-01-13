package com.shootr.mobile.domain.model;

public interface PushSettingType {

  String STARTED_SHOOTING = "STARTEDSHOOTING";
  String NICE_SHOT = "NICESHOT";
  String SHARED_SHOT = "SHAREDSHOT";
  String NEW_FOLLOWERS = "STARTFOLLOW";
  String CHECK_IN = "CHECKIN";
  String POLL = "VOTED_IN_POLL";
  String PRIVATE_MESSAGE = "PRIVATE_MESSAGE";

  String OFF = "NONE";
  String ALL = "ALL";
  String FAVORITE_STREAMS = "FAVORITE_STREAMS";
  String FOLLOWING = "FOLLOWING";

  String[] TYPES_OFF_FAVORITE_ALL = {
      OFF, FAVORITE_STREAMS, ALL
  };

  String[] TYPES_OFF_FOLLOWING_ALL = {
      OFF, FOLLOWING, ALL
  };
}
