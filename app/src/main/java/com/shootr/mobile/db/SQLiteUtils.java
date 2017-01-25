package com.shootr.mobile.db;

public class SQLiteUtils {

    private SQLiteUtils() {
    }

    public static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.UserTable.TABLE
      + " ("
      + DatabaseContract.UserTable.ID
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.UserTable.USER_NAME
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.EMAIL
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.EMAIL_CONFIRMED
      + " INT NULL,"
      + DatabaseContract.UserTable.USER_VERIFIED
      + " INT NULL,"
      + DatabaseContract.UserTable.NAME
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.PHOTO
      + " VARCHAR(1024) NULL,"
      + DatabaseContract.UserTable.POINTS
      + " INT NULL,"
      + DatabaseContract.UserTable.NUM_FOLLOWINGS
      + " INT NOT NULL,"
      + DatabaseContract.UserTable.NUM_FOLLOWERS
      + " INT NOT NULL,"
      + DatabaseContract.UserTable.WEBSITE
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.RANK
      + " INT,"
      + DatabaseContract.UserTable.BIO
      + " VARCHAR(150),"
      + DatabaseContract.UserTable.NAME_NORMALIZED
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.USER_NAME_NORMALIZED
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.EMAIL_NORMALIZED
      + " VARCHAR(255) NULL,"
      + DatabaseContract.UserTable.ID_WATCHING_STREAM
      + " TEXT NULL,"
      + DatabaseContract.UserTable.WATCHING_STREAM_TITLE
      + " TEXT NULL,"
      + DatabaseContract.UserTable.JOIN_STREAM_DATE
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.WATCHING_SYNCHRONIZED
      + " TEXT NULL,"
      + DatabaseContract.UserTable.CREATED_STREAMS_COUNT
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.FAVORITED_STREAMS_COUNT
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.SOCIAL_LOGIN
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.RECEIVED_REACTIONS
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.ANALYTICS_USER_TYPE
      + " TEXT NULL,"
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1));";

    public static final String CREATE_TABLE_SHOT = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.ShotTable.TABLE
      + " ("
      + DatabaseContract.ShotTable.ID_SHOT
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.ShotTable.ID_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.ShotTable.USERNAME
      + " TEXT NOT NULL,"
      + DatabaseContract.ShotTable.USER_PHOTO
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.COMMENT
      + " VARCHAR(255) NULL,"
      + DatabaseContract.ShotTable.IMAGE
      + " VARCHAR(255) NULL,"
      + DatabaseContract.ShotTable.IMAGE_WIDTH
      + " VARCHAR(255) NULL,"
      + DatabaseContract.ShotTable.IMAGE_HEIGHT
      + " VARCHAR(255) NULL,"
      + DatabaseContract.ShotTable.ID_STREAM
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.STREAM_TITLE
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.NICE_COUNT
      + " NUMBER NOT NULL,"
      + DatabaseContract.ShotTable.TYPE
      + " TEXT NOT NULL,"
      + DatabaseContract.ShotTable.ID_SHOT_PARENT
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.ID_USER_PARENT
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.USERNAME_PARENT
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.VIDEO_URL
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.VIDEO_TITLE
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.VIDEO_DURATION
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.PROFILE_HIDDEN
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.REPLY_COUNT
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.VIEWS
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.LINK_CLICKS
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.RESHOOT_COUNT
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.CTA_CAPTION
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.CTA_BUTTON_LINK
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.CTA_BUTTON_TEXT
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.PROMOTED
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.VERIFIED_USER
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.IS_PADDING
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.FROM_HOLDER
      + " NUMBER NULL,"
      + DatabaseContract.ShotTable.FROM_CONTRIBUTOR
      + " NUMBER NULL,"
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME NULL,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_SHOT_QUEUE = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.ShotQueueTable.TABLE
      + " ("
      + DatabaseContract.ShotQueueTable.ID_QUEUE
      + " INTEGER PRIMARY KEY AUTOINCREMENT,"
      + DatabaseContract.ShotQueueTable.FAILED
      + " INT NOT NULL,"
      + DatabaseContract.ShotQueueTable.IMAGE_FILE
      + " TEXT NULL,"
      + DatabaseContract.ShotQueueTable.ID_SHOT
      + " TEXT NULL,"
      + DatabaseContract.ShotQueueTable.ID_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.ShotQueueTable.USERNAME
      + " TEXT NOT NULL,"
      + DatabaseContract.ShotQueueTable.USER_PHOTO
      + " TEXT NULL,"
      + DatabaseContract.ShotQueueTable.COMMENT
      + " VARCHAR(255) NULL,"
      + DatabaseContract.ShotQueueTable.IMAGE
      + " VARCHAR(255) NULL,"
      + DatabaseContract.ShotQueueTable.ID_STREAM
      + " TEXT NULL,"
      + DatabaseContract.ShotQueueTable.STREAM_TITLE
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.TYPE
      + " TEXT NOT NULL,"
      + DatabaseContract.ShotTable.ID_SHOT_PARENT
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.ID_USER_PARENT
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.USERNAME_PARENT
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.VIDEO_URL
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.VIDEO_TITLE
      + " TEXT NULL,"
      + DatabaseContract.ShotTable.VIDEO_DURATION
      + " NUMBER NULL,"
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME NULL,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1) NULL"
      +
      ")";

    public static final String CREATE_TABLE_MESSAGE_QUEUE = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.MessageQueueTable.TABLE
        + " ("
        + DatabaseContract.MessageQueueTable.ID_QUEUE
        + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + DatabaseContract.MessageQueueTable.FAILED
        + " INT NOT NULL,"
        + DatabaseContract.MessageQueueTable.IMAGE_FILE
        + " TEXT NULL,"
        + DatabaseContract.MessageQueueTable.ID_PRIVATE_MESSAGE
        + " TEXT NULL,"
        + DatabaseContract.MessageQueueTable.ID_USER
        + " TEXT NOT NULL,"
        + DatabaseContract.MessageQueueTable.ID_TARGET_USER
        + " TEXT NOT NULL,"
        + DatabaseContract.MessageQueueTable.USERNAME
        + " TEXT NOT NULL,"
        + DatabaseContract.MessageQueueTable.COMMENT
        + " VARCHAR(255) NULL,"
        + DatabaseContract.MessageQueueTable.IMAGE
        + " VARCHAR(255) NULL,"
        + DatabaseContract.MessageQueueTable.ID_PRIVATE_MESSAGE_CHANNEL
        + " TEXT NULL,"
        + DatabaseContract.MessageQueueTable.TITLE
        + " TEXT NULL,"
        + DatabaseContract.MessageQueueTable.VIDEO_URL
        + " TEXT NULL,"
        + DatabaseContract.MessageQueueTable.VIDEO_TITLE
        + " TEXT NULL,"
        + DatabaseContract.MessageQueueTable.VIDEO_DURATION
        + " NUMBER NULL,"
        + DatabaseContract.SyncColumns.BIRTH
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.MODIFIED
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.DELETED
        + " DATETIME NULL,"
        + DatabaseContract.SyncColumns.REVISION
        + " INT NOT NULL,"
        + DatabaseContract.SyncColumns.SYNCHRONIZED
        + " CHAR(1) NULL"
        +
        ")";

    public static final String CREATE_TABLE_FOLLOW = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.FollowTable.TABLE
      + " ("
      + DatabaseContract.FollowTable.ID_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.FollowTable.ID_FOLLOWED_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.FollowTable.IS_FRIEND
      + " NUMBER,"
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME NULL,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1) NULL,"
      + " PRIMARY KEY("
      + DatabaseContract.FollowTable.ID_USER
      + ","
      + DatabaseContract.FollowTable.ID_FOLLOWED_USER
      + "))";

    public static final String CREATE_TABLE_BLOCK = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.BlockTable.TABLE
      + " ("
      + DatabaseContract.BlockTable.ID_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.BlockTable.ID_BLOCKED_USER
      + " TEXT NOT NULL,"
      + " PRIMARY KEY("
      + DatabaseContract.BlockTable.ID_USER
      + ","
      + DatabaseContract.BlockTable.ID_BLOCKED_USER
      + "))";

    public static final String CREATE_TABLE_BAN = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.BanTable.TABLE
      + " ("
      + DatabaseContract.BanTable.ID_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.BanTable.ID_BANNED_USER
      + " TEXT NOT NULL,"
      + " PRIMARY KEY("
      + DatabaseContract.BanTable.ID_USER
      + ","
      + DatabaseContract.BanTable.ID_BANNED_USER
      + "))";

    public static final String CREATE_TABLE_MUTE = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.MuteTable.TABLE
      + " ("
      + DatabaseContract.MuteTable.ID_MUTED_STREAM
      + " TEXT NOT NULL PRIMARY KEY, "
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME NULL,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_DEVICE = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.DeviceTable.TABLE
      + " ("
      + DatabaseContract.DeviceTable.ID_DEVICE
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.DeviceTable.PLATFORM
      + " TEXT NOT NULL,"
      + DatabaseContract.DeviceTable.TOKEN
      + " VARCHAR(255) NULL,"
      + DatabaseContract.DeviceTable.UNIQUE_DEVICE_ID
      + " VARCHAR(255) NULL,"
      + DatabaseContract.DeviceTable.MODEL
      + " VARCHAR(255) NULL,"
      + DatabaseContract.DeviceTable.OS_VERSION
      + " VARCHAR(255),"
      + DatabaseContract.DeviceTable.APP_VERSION
      + " VARCHAR(255),"
      + DatabaseContract.DeviceTable.LOCALE
      + " VARCHAR(255))";

    public static final String CREATE_TABLE_STREAM = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.StreamTable.TABLE
      + " ("
      + DatabaseContract.StreamTable.ID_STREAM
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.StreamTable.ID_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.StreamTable.ID_USER_STREAM
      + " TEXT,"
      + DatabaseContract.StreamTable.USERNAME
      + " TEXT NOT NULL,"
      + DatabaseContract.StreamTable.TITLE
      + " VARCHAR(255) NOT NULL,"
      + DatabaseContract.StreamTable.PHOTO
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.LANDSCAPE_PHOTO
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.DESCRIPTION
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.TOPIC
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.LAST_UPDATED_USER
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.COUNTRY
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.MEDIA_COUNT
      + " INTEGER NULL,"
      + DatabaseContract.StreamTable.REMOVED
      + " INT NOT NULL,"
      + DatabaseContract.StreamTable.TOTAL_FAVORITES
      + " INTEGER NOT NULL,"
      + DatabaseContract.StreamTable.TOTAL_WATCHERS
      + " INTEGER NOT NULL,"
      + DatabaseContract.StreamTable.HISTORIC_WATCHERS
      + " INTEGER NULL,"
      + DatabaseContract.StreamTable.TOTAL_SHOTS
      + " NUMBER NULL,"
      + DatabaseContract.StreamTable.UNIQUE_SHOTS
      + " NUMBER NULL,"
      + DatabaseContract.StreamTable.READ_WRITE_MODE
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.VERIFIED_USER
      + " NUMBER NULL,"
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME NULL,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_STREAM_SEARCH = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.StreamSearchTable.TABLE
      + " ("
      + DatabaseContract.StreamTable.ID_STREAM
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.StreamTable.ID_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.StreamTable.USERNAME
      + " TEXT NOT NULL,"
      + DatabaseContract.StreamTable.TITLE
      + " VARCHAR(255) NOT NULL,"
      + DatabaseContract.StreamTable.PHOTO
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.LANDSCAPE_PHOTO
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.COUNTRY
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.DESCRIPTION
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.TOPIC
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.MEDIA_COUNT
      + " INTEGER NULL,"
      + DatabaseContract.StreamTable.REMOVED
      + " INT NOT NULL,"
      + DatabaseContract.StreamTable.TOTAL_FAVORITES
      + " INTEGER NOT NULL,"
      + DatabaseContract.StreamTable.TOTAL_WATCHERS
      + " INTEGER NOT NULL,"
      + DatabaseContract.StreamTable.HISTORIC_WATCHERS
      + " INTEGER NULL,"
      + DatabaseContract.StreamTable.TOTAL_SHOTS
      + " NUMBER NULL,"
      + DatabaseContract.StreamTable.UNIQUE_SHOTS
      + " NUMBER NULL,"
      + DatabaseContract.StreamTable.READ_WRITE_MODE
      + " TEXT NULL,"
      + DatabaseContract.StreamTable.VERIFIED_USER
      + " NUMBER NULL,"
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME NULL,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1) NULL,"
      + DatabaseContract.StreamSearchTable.WATCHERS
      + " INT NOT NULL)";

    public static final String CREATE_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.FavoriteTable.TABLE
      + " ("
      + DatabaseContract.FavoriteTable.ID_STREAM
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.FavoriteTable.ORDER
      + " NUMBER NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " TEXT NULL)";

    public static final String CREATE_TABLE_ACTIVITY = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.ActivityTable.TABLE
      + " ("
      + DatabaseContract.ActivityTable.ID_ACTIVITY
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.ActivityTable.ID_USER
      + " TEXT NOT NULL,"
      + DatabaseContract.ActivityTable.ID_TARGET_USER
      + " TEXT NULL,"
      + DatabaseContract.ActivityTable.USERNAME
      + " TEXT NOT NULL,"
      + DatabaseContract.ActivityTable.ID_STREAM
      + " TEXT NULL,"
      + DatabaseContract.ActivityTable.USER_PHOTO
      + " TEXT NULL,"
      + DatabaseContract.ActivityTable.STREAM_TITLE
      + " VARCHAR(255) NULL,"
      + DatabaseContract.ActivityTable.COMMENT
      + " VARCHAR(255) NULL,"
      + DatabaseContract.ActivityTable.TYPE
      + " TEXT NULL,"
      + DatabaseContract.ActivityTable.ID_SHOT
      + " TEXT NULL,"
      + DatabaseContract.ActivityTable.ID_STREAM_AUTHOR
      + " TEXT NULL,"
      + DatabaseContract.ActivityTable.ID_POLL
      + " TEXT NULL,"
      + DatabaseContract.ActivityTable.POLL_QUESTION
      + " TEXT NULL,"
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME NULL,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1) NULL)";

    public static final String CREATE_ME_TABLE_ACTIVITY = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.MeActivityTable.TABLE
        + " ("
        + DatabaseContract.MeActivityTable.ID_ACTIVITY
        + " TEXT NOT NULL PRIMARY KEY,"
        + DatabaseContract.MeActivityTable.ID_USER
        + " TEXT NOT NULL,"
        + DatabaseContract.MeActivityTable.ID_TARGET_USER
        + " TEXT NULL,"
        + DatabaseContract.MeActivityTable.USERNAME
        + " TEXT NOT NULL,"
        + DatabaseContract.MeActivityTable.ID_STREAM
        + " TEXT NULL,"
        + DatabaseContract.MeActivityTable.USER_PHOTO
        + " TEXT NULL,"
        + DatabaseContract.MeActivityTable.STREAM_TITLE
        + " VARCHAR(255) NULL,"
        + DatabaseContract.MeActivityTable.COMMENT
        + " VARCHAR(255) NULL,"
        + DatabaseContract.MeActivityTable.TYPE
        + " TEXT NULL,"
        + DatabaseContract.MeActivityTable.ID_SHOT
        + " TEXT NULL,"
        + DatabaseContract.MeActivityTable.ID_STREAM_AUTHOR
        + " TEXT NULL,"
        + DatabaseContract.MeActivityTable.ID_POLL
        + " TEXT NULL,"
        + DatabaseContract.MeActivityTable.POLL_QUESTION
        + " TEXT NULL,"
        + DatabaseContract.SyncColumns.BIRTH
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.MODIFIED
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.DELETED
        + " DATETIME NULL,"
        + DatabaseContract.SyncColumns.REVISION
        + " INT NOT NULL,"
        + DatabaseContract.SyncColumns.SYNCHRONIZED
        + " CHAR(1) NULL)";

    public static final String CREATE_SUGGESTED_PEOPLE = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.SuggestedPeopleTable.TABLE
      + " ("
      + DatabaseContract.UserTable.ID
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.UserTable.USER_NAME
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.EMAIL
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.EMAIL_CONFIRMED
      + " INT NULL,"
      + DatabaseContract.UserTable.USER_VERIFIED
      + " INT NULL,"
      + DatabaseContract.UserTable.NAME
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.PHOTO
      + " VARCHAR(1024) NULL,"
      + DatabaseContract.UserTable.POINTS
      + " INT NULL,"
      + DatabaseContract.UserTable.NUM_FOLLOWINGS
      + " INT NOT NULL,"
      + DatabaseContract.UserTable.NUM_FOLLOWERS
      + " INT NOT NULL,"
      + DatabaseContract.UserTable.WEBSITE
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.RANK
      + " INT,"
      + DatabaseContract.UserTable.BIO
      + " VARCHAR(150),"
      + DatabaseContract.UserTable.NAME_NORMALIZED
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.USER_NAME_NORMALIZED
      + " VARCHAR(255),"
      + DatabaseContract.UserTable.EMAIL_NORMALIZED
      + " VARCHAR(255) NULL,"
      + DatabaseContract.UserTable.ID_WATCHING_STREAM
      + " TEXT NULL,"
      + DatabaseContract.UserTable.WATCHING_STREAM_TITLE
      + " TEXT NULL,"
      + DatabaseContract.UserTable.JOIN_STREAM_DATE
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.WATCHING_SYNCHRONIZED
      + " TEXT NULL,"
      + DatabaseContract.SuggestedPeopleTable.RELEVANCE
      + " INT NOT NULL,"
      + DatabaseContract.UserTable.CREATED_STREAMS_COUNT
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.FAVORITED_STREAMS_COUNT
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.SOCIAL_LOGIN
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.RECEIVED_REACTIONS
      + " INTEGER NULL,"
      + DatabaseContract.UserTable.ANALYTICS_USER_TYPE
      + " TEXT NULL,"
      + DatabaseContract.SyncColumns.BIRTH
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.MODIFIED
      + " DATETIME NOT NULL,"
      + DatabaseContract.SyncColumns.DELETED
      + " DATETIME,"
      + DatabaseContract.SyncColumns.REVISION
      + " INT NOT NULL,"
      + DatabaseContract.SyncColumns.SYNCHRONIZED
      + " CHAR(1));";

    public static final String CREATE_TABLE_NICE_SHOTS = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.NiceShotTable.TABLE
      + " ("
      + DatabaseContract.NiceShotTable.ID_SHOT
      + " TEXT NOT NULL PRIMARY KEY)";

    public static final String CREATE_TABLE_CONTRIBUTOR = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.ContributorTable.TABLE
        + " ("
        + DatabaseContract.ContributorTable.ID_STREAM
        + " TEXT NOT NULL,"
        + DatabaseContract.ContributorTable.ID_USER
        + " TEXT NOT NULL,"
        + DatabaseContract.ContributorTable.ID_CONTRIBUTOR
        + " TEXT NOT NULL PRIMARY KEY)";


    public static final String CREATE_TABLE_TIMELINE_SYNC = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.TimelineSyncTable.TABLE
      + " ("
      + DatabaseContract.TimelineSyncTable.STREAM_ID
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.TimelineSyncTable.DATE
      + " DATETIME NOT NULL);";

    public static final String CREATE_TABLE_POLL = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.PollTable.TABLE
        + " ("
        + DatabaseContract.PollTable.ID_POLL
        + " TEXT NOT NULL PRIMARY KEY,"
        + DatabaseContract.PollTable.ID_STREAM
        + " TEXT NOT NULL,"
        + DatabaseContract.PollTable.ID_USER
        + " TEXT NOT NULL,"
        + DatabaseContract.PollTable.QUESTION
        + " TEXT NOT NULL,"
        + DatabaseContract.PollTable.HAS_VOTED
        + " NUMBER NULL,"
        + DatabaseContract.PollTable.STATUS
        + " TEXT NOT NULL,"
        + DatabaseContract.PollTable.VOTE_STATUS
        + " TEXT NOT NULL,"
        + DatabaseContract.PollTable.PUBLISHED
        + " NUMBER NOT NULL)";

    public static final String CREATE_TABLE_POLL_OPTION = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.PollOptionTable.TABLE
        + " ("
        + DatabaseContract.PollOptionTable.ID_POLL_OPTION
        + " TEXT NOT NULL PRIMARY KEY,"
        + DatabaseContract.PollOptionTable.ID_POLL
        + " TEXT NOT NULL,"
        + DatabaseContract.PollOptionTable.IMAGE_URL
        + " TEXT NULL,"
        + DatabaseContract.PollOptionTable.TEXT
        + " TEXT NOT NULL,"
        + DatabaseContract.PollOptionTable.ORDER
        + " INT NOT NULL,"
        + DatabaseContract.PollOptionTable.VOTES
        + " NUMBER NULL)";

    public static final String CREATE_TABLE_HIGHTLIGHTED_SHOT = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.HighlightedShotTable.TABLE
        + " ("
        + DatabaseContract.HighlightedShotTable.ID_HIGHLIGHTED_SHOT
        + " TEXT NOT NULL PRIMARY KEY,"
        + DatabaseContract.HighlightedShotTable.ACTIVE
        + " LONG NOT NULL,"
        + DatabaseContract.HighlightedShotTable.VISIBLE
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.ID_SHOT
        + " TEXT NOT NULL,"
        + DatabaseContract.HighlightedShotTable.ID_USER
        + " TEXT NOT NULL,"
        + DatabaseContract.HighlightedShotTable.USERNAME
        + " TEXT NOT NULL,"
        + DatabaseContract.HighlightedShotTable.USER_PHOTO
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.COMMENT
        + " VARCHAR(255) NULL,"
        + DatabaseContract.HighlightedShotTable.IMAGE
        + " VARCHAR(255) NULL,"
        + DatabaseContract.HighlightedShotTable.IMAGE_WIDTH
        + " VARCHAR(255) NULL,"
        + DatabaseContract.HighlightedShotTable.IMAGE_HEIGHT
        + " VARCHAR(255) NULL,"
        + DatabaseContract.HighlightedShotTable.ID_STREAM
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.STREAM_TITLE
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.NICE_COUNT
        + " NUMBER NOT NULL,"
        + DatabaseContract.HighlightedShotTable.TYPE
        + " TEXT NOT NULL,"
        + DatabaseContract.HighlightedShotTable.ID_SHOT_PARENT
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.ID_USER_PARENT
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.USERNAME_PARENT
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.VIDEO_URL
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.VIDEO_TITLE
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.VIDEO_DURATION
        + " NUMBER NULL,"
        + DatabaseContract.HighlightedShotTable.PROFILE_HIDDEN
        + " NUMBER NULL,"
        + DatabaseContract.HighlightedShotTable.REPLY_COUNT
        + " NUMBER NULL,"
        + DatabaseContract.HighlightedShotTable.VIEWS
        + " NUMBER NULL,"
        + DatabaseContract.HighlightedShotTable.LINK_CLICKS
        + " NUMBER NULL,"
        + DatabaseContract.HighlightedShotTable.RESHOOT_COUNT
        + " NUMBER NULL,"
        + DatabaseContract.HighlightedShotTable.CTA_CAPTION
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.CTA_BUTTON_LINK
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.CTA_BUTTON_TEXT
        + " TEXT NULL,"
        + DatabaseContract.HighlightedShotTable.PROMOTED
        + " NUMBER NULL,"
        + DatabaseContract.HighlightedShotTable.VERIFIED_USER
        + " NUMBER NULL,"
        + DatabaseContract.HighlightedShotTable.IS_PADDING
        + " NUMBER NULL,"
        + DatabaseContract.SyncColumns.BIRTH
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.MODIFIED
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.DELETED
        + " DATETIME NULL,"
        + DatabaseContract.SyncColumns.REVISION
        + " INT NOT NULL,"
        + DatabaseContract.SyncColumns.SYNCHRONIZED
        + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_RECENT_STREAM = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.RecentStreamTable.TABLE
        + " ("
        + DatabaseContract.RecentStreamTable.ID_STREAM
        + " TEXT NOT NULL PRIMARY KEY,"
        + DatabaseContract.RecentStreamTable.JOIN_STREAM_DATE
        + " INTEGER NULL)";

    public static final String CREATE_TABLE_SHOT_EVENT = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.ShotEventTable.TABLE
        + " ("
        + DatabaseContract.ShotEventTable.ID_SHOT
        + " TEXT NOT NULL,"
        + DatabaseContract.ShotEventTable.TYPE
        + " TEXT NULL,"
        + DatabaseContract.ShotEventTable.TIMESTAMP
        + " INTEGER NULL)";

    public static final String CREATE_TABLE_SYNCHRO = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.SynchroTable.TABLE
        + " ("
        + DatabaseContract.SynchroTable.ENTITY
        + " TEXT NOT NULL PRIMARY KEY,"
        + DatabaseContract.SynchroTable.TIMESTAMP
        + " INTEGER NULL)";

    public static final String CREATE_TABLE_PRIVATE_MESSAGE_CHANNEL = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.PrivateMessageChannelTable.TABLE
        + " ("
        + DatabaseContract.PrivateMessageChannelTable.ID_PRIVATE_MESSAGE_CHANNEL
        + " TEXT NOT NULL PRIMARY KEY,"
        + DatabaseContract.PrivateMessageChannelTable.ID_TARGET_USER
        + " TEXT NOT NULL,"
        + DatabaseContract.PrivateMessageChannelTable.TITLE
        + " TEXT NOT NULL,"
        + DatabaseContract.PrivateMessageChannelTable.IMAGE
        + " TEXT NULL,"
        + DatabaseContract.PrivateMessageChannelTable.READ
        + " NUMBER NULL,"
        + DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_TIME
        + " INTEGER NULL,"
        + DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_COMMENT
        + " TEXT NULL,"
        + DatabaseContract.SyncColumns.BIRTH
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.MODIFIED
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.DELETED
        + " DATETIME NULL,"
        + DatabaseContract.SyncColumns.REVISION
        + " INT NOT NULL,"
        + DatabaseContract.SyncColumns.SYNCHRONIZED
        + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_PRIVATE_MESSAGE = "CREATE TABLE IF NOT EXISTS "
        + DatabaseContract.PrivateMessageTable.TABLE
        + " ("
        + DatabaseContract.PrivateMessageTable.ID_PRIVATE_MESSAGE
        + " TEXT NOT NULL PRIMARY KEY,"
        + DatabaseContract.PrivateMessageTable.ID_PRIVATE_MESSAGE_CHANNEL
        + " TEXT NOT NULL,"
        + DatabaseContract.PrivateMessageTable.ID_USER
        + " TEXT NOT NULL,"
        + DatabaseContract.PrivateMessageTable.USERNAME
        + " TEXT NOT NULL,"
        + DatabaseContract.PrivateMessageTable.COMMENT
        + " VARCHAR(255) NULL,"
        + DatabaseContract.PrivateMessageTable.IMAGE
        + " VARCHAR(255) NULL,"
        + DatabaseContract.PrivateMessageTable.IMAGE_WIDTH
        + " VARCHAR(255) NULL,"
        + DatabaseContract.PrivateMessageTable.IMAGE_HEIGHT
        + " VARCHAR(255) NULL,"
        + DatabaseContract.PrivateMessageTable.VIDEO_URL
        + " TEXT NULL,"
        + DatabaseContract.PrivateMessageTable.VIDEO_TITLE
        + " TEXT NULL,"
        + DatabaseContract.PrivateMessageTable.VIDEO_DURATION
        + " NUMBER NULL,"
        + DatabaseContract.PrivateMessageTable.VERIFIED_USER
        + " NUMBER NULL,"
        + DatabaseContract.SyncColumns.BIRTH
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.MODIFIED
        + " DATETIME NOT NULL,"
        + DatabaseContract.SyncColumns.DELETED
        + " DATETIME NULL,"
        + DatabaseContract.SyncColumns.REVISION
        + " INT NOT NULL,"
        + DatabaseContract.SyncColumns.SYNCHRONIZED
        + " CHAR(1) NULL)";

}
