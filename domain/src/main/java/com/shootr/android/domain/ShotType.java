package com.shootr.android.domain;

public interface ShotType {

    String COMMENT = "COMMENT";
    String CHECKIN = "CHECKIN";
    String START_FOLLOW = "STARTFOLLOW";
    String LISTED_EVENT = "LISTEDEVENT";
    String PROFILE_UPDATED = "PROFILEUPDATED";
    String JOIN_EVENT = "JOINEVENT";
    String EXIT_EVENT = "EXITEVENT";
    String UPDATE_EVENT = "UPDATEEVENT";

    String[] TYPES_HIDDEN = { JOIN_EVENT, EXIT_EVENT, UPDATE_EVENT };
    String[] TYPES_SYNC_TRIGGER = { PROFILE_UPDATED, JOIN_EVENT, EXIT_EVENT, UPDATE_EVENT };
}
