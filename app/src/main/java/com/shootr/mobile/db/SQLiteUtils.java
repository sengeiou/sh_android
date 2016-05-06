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

    public static final String CREATE_TABLE_TIMELINE_SYNC = "CREATE TABLE IF NOT EXISTS "
      + DatabaseContract.TimelineSyncTable.TABLE
      + " ("
      + DatabaseContract.TimelineSyncTable.STREAM_ID
      + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.TimelineSyncTable.DATE
      + " DATETIME NOT NULL);";
}
