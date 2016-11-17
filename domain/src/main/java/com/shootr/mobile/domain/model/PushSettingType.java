package com.shootr.mobile.domain.model;

public interface PushSettingType {

  String STARTED_SHOOTING = "STARTEDSHOOTING";
  String NICE_SHOT = "NICESHOT";
  String SHARED_SHOT = "SHAREDSHOT";
  String NEW_FOLLOWERS = "STARTFOLLOW";
  String CHECK_IN = "CHECKIN";
  String POLL = "VOTED_IN_POLL";

  String OFF = "NONE";
  String ALL = "ALL";
  String FAVORITE_STREAMS = "FAVORITE_STREAMS";
  String FOLLOWING = "FOLLOWING";


  String[] TYPES_STARTED_SHOOTING = {
      OFF, FAVORITE_STREAMS, ALL
  };

  String[] TYPES_NICE_SHOT = {
      OFF, FOLLOWING, ALL
  };

  String[] TYPES_RESHOT = {
      OFF, FOLLOWING, ALL
  };

  String[] TYPES_POLL = {
      OFF, FAVORITE_STREAMS, ALL
  };

  String[] TYPES_CHECKIN = {
      OFF, FAVORITE_STREAMS, ALL
  };

  String[] TYPES_NEW_FOLLOWERS = {
      OFF, FOLLOWING, ALL
  };
}
