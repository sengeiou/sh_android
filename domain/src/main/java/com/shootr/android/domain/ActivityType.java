package com.shootr.android.domain;

public interface ActivityType {

    String COMMENT = "COMMENT";
    String CHECKIN = "CHECKIN";
    String START_FOLLOW = "STARTFOLLOW";
    String LISTED_EVENT = "LISTEDEVENT";
    String PROFILE_UPDATED = "PROFILEUPDATED";
    String JOIN_EVENT = "JOINEVENT";
    String EXIT_EVENT = "EXITEVENT";
    String UPDATE_EVENT = "UPDATEEVENT";

    String[] TYPES_ACTIVITY = { CHECKIN,
      EXIT_EVENT,
      JOIN_EVENT,
      LISTED_EVENT,
      PROFILE_UPDATED,
      START_FOLLOW,
      UPDATE_EVENT };
    String[] TYPES_ACTIVITY_SHOWN = { CHECKIN,
      LISTED_EVENT,
      PROFILE_UPDATED,
      START_FOLLOW};

}
