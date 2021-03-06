package com.shootr.mobile.domain.model.activity;

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
    String REPLY_SHOT = "REPLY";
    String POLL_PUBLISHED = "POLLPUBLISHED";
    String VOTED_IN_POLL = "VOTEDINPOLL";
    String SHARE_POLL = "SHAREDPOLL";
    String FINISHED_POLL = "POLLFINISHED";
    String WAKE_UP_SHOT = "WAKEUPSHOT";
    String IMPORTANT_STARTED_SHOOTING = "IMPORTANT_STARTED_SHOOTING";

    String[] TYPES_ACTIVITY = {
      CHECKIN, NICE_SHOT, EXIT_STREAM, JOIN_STREAM, OPENED_STREAM, PROFILE_UPDATED, START_FOLLOW, UPDATE_STREAM,
      STARTED_SHOOTING, SHARE_STREAM, SHARE_SHOT, STREAM_FAVORITED, MENTION, REPLY_SHOT, POLL_PUBLISHED,
        VOTED_IN_POLL, SHARE_POLL, FINISHED_POLL, WAKE_UP_SHOT, IMPORTANT_STARTED_SHOOTING
    };

    String[] TYPES_ACTIVITY_SHOWN = {
      NICE_SHOT, OPENED_STREAM, PROFILE_UPDATED, START_FOLLOW, STARTED_SHOOTING, SHARE_STREAM, SHARE_SHOT,
      STREAM_FAVORITED, MENTION, REPLY_SHOT, POLL_PUBLISHED, VOTED_IN_POLL, SHARE_POLL, FINISHED_POLL,
        WAKE_UP_SHOT, CHECKIN, IMPORTANT_STARTED_SHOOTING
    };
}
