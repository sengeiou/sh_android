package com.shootr.mobile.domain;

public interface ActivityType {

    String CHECKIN = "CHECKIN";
    String START_FOLLOW = "STARTFOLLOW";
    String OPENED_STREAM = "OPENEDSTREAM";
    String PROFILE_UPDATED = "PROFILEUPDATED";
    String JOIN_STREAM = "JOINSTREAM";
    String EXIT_STREAM = "EXITSTREAM";
    String UPDATE_STREAM = "UPDATESTREAM";
    String STARTED_SHOOTING = "STARTEDSHOOTING";
    String NICE_SHOT = "NICESHOT";
    String SHARE_STREAM = "SHAREDSTREAM";
    String SHARE_SHOT = "SHAREDSHOT";
    String STREAM_FAVORITED = "FAVORITESTREAM";
    String MENTION = "MENTION";

    String[] TYPES_ACTIVITY = { CHECKIN,
      NICE_SHOT,
      EXIT_STREAM,
      JOIN_STREAM, OPENED_STREAM,
      PROFILE_UPDATED,
      START_FOLLOW,
      UPDATE_STREAM,
      STARTED_SHOOTING,
      SHARE_STREAM,
      SHARE_SHOT,
      STREAM_FAVORITED,
      MENTION,
    };

    String[] TYPES_ACTIVITY_SHOWN = { CHECKIN,
      NICE_SHOT, OPENED_STREAM,
      PROFILE_UPDATED,
      START_FOLLOW,
      STARTED_SHOOTING,
      SHARE_STREAM,
      SHARE_SHOT,
      STREAM_FAVORITED,
      MENTION,
    };

    String[] TYPES_SYNC_TRIGGER = { PROFILE_UPDATED,
      JOIN_STREAM,
      EXIT_STREAM,
      UPDATE_STREAM };

}