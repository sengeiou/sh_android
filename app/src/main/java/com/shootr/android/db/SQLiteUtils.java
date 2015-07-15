package com.shootr.android.db;

import com.shootr.android.db.DatabaseContract.DeviceTable;
import com.shootr.android.db.DatabaseContract.FollowTable;
import com.shootr.android.db.DatabaseContract.ShotQueueTable;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.DatabaseContract.SyncColumns;
import com.shootr.android.db.DatabaseContract.TablesSync;
import com.shootr.android.db.DatabaseContract.UserTable;

public class SQLiteUtils {

    private SQLiteUtils() {
    }

    public static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + UserTable.TABLE + " ("
            + UserTable.ID + " TEXT NOT NULL PRIMARY KEY,"
            + UserTable.SESSION_TOKEN + " VARCHAR(255),"
            + UserTable.USER_NAME + " VARCHAR(255),"
            + UserTable.EMAIL + " VARCHAR(255),"
            + UserTable.NAME + " VARCHAR(255),"
            + UserTable.PHOTO + " VARCHAR(1024) NULL,"
            + UserTable.POINTS+" INT NULL,"
            + UserTable.NUM_FOLLOWINGS+" INT NOT NULL,"
            + UserTable.NUM_FOLLOWERS+" INT NOT NULL,"
            + UserTable.WEBSITE+" VARCHAR(255),"
            + UserTable.RANK+" INT,"
            + UserTable.BIO+" VARCHAR(150),"
            + UserTable.NAME_NORMALIZED+" VARCHAR(255),"
            + UserTable.USER_NAME_NORMALIZED+" VARCHAR(255),"
            + UserTable.EMAIL_NORMALIZED+" VARCHAR(255) NULL,"
            + UserTable.WATCHING_EVENT_ID +" TEXT NULL,"
            + UserTable.WATCHING_EVENT_TITLE +" TEXT NULL,"
            + UserTable.JOIN_EVENT_DATE+" INTEGER NULL,"
            + SyncColumns.BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.DELETED + " DATETIME,"
            + SyncColumns.REVISION + " INT NOT NULL,"
            + SyncColumns.SYNCHRONIZED + " CHAR(1));";

    public static final String CREATE_TABLE_SHOT = "CREATE TABLE IF NOT EXISTS " + ShotTable.TABLE + " ("
            + ShotTable.ID_SHOT + " TEXT NOT NULL PRIMARY KEY,"
            + ShotTable.ID_USER + " TEXT NOT NULL,"
            + ShotTable.USERNAME + " TEXT NOT NULL,"
            + ShotTable.USER_PHOTO + " TEXT NULL,"
            + ShotTable.COMMENT + " VARCHAR(255) NULL,"
            + ShotTable.IMAGE + " VARCHAR(255) NULL,"
            + ShotTable.ID_EVENT+ " TEXT NULL,"
            + ShotTable.EVENT_TAG+ " TEXT NULL,"
            + ShotTable.EVENT_TITLE+ " TEXT NULL,"
            + ShotTable.TYPE+ " TEXT NOT NULL,"
            + ShotTable.ID_SHOT_PARENT+ " TEXT NULL,"
            + ShotTable.ID_USER_PARENT+ " TEXT NULL,"
            + ShotTable.USERNAME_PARENT+ " TEXT NULL,"
            + ShotTable.VIDEO_URL+ " TEXT NULL,"
            + ShotTable.VIDEO_TITLE+ " TEXT NULL,"
            + ShotTable.VIDEO_DURATION+ " NUMBER NULL,"
            + SyncColumns.BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.DELETED + " DATETIME NULL,"
            + SyncColumns.REVISION + " INT NOT NULL,"
            + SyncColumns.SYNCHRONIZED + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_SHOT_QUEUE = "CREATE TABLE IF NOT EXISTS " + ShotQueueTable.TABLE + " ("
          + ShotQueueTable.ID_QUEUE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + ShotQueueTable.FAILED + " INT NOT NULL,"
          + ShotQueueTable.IMAGE_FILE+ " TEXT NULL,"
          + ShotQueueTable.ID_SHOT + " TEXT NULL,"
          + ShotQueueTable.ID_USER + " TEXT NOT NULL,"
          + ShotQueueTable.USERNAME + " TEXT NOT NULL,"
          + ShotQueueTable.USER_PHOTO + " TEXT NULL,"
          + ShotQueueTable.COMMENT + " VARCHAR(255) NULL,"
          + ShotQueueTable.IMAGE + " VARCHAR(255) NULL,"
          + ShotQueueTable.ID_EVENT+ " TEXT NULL,"
          + ShotQueueTable.EVENT_TAG+ " TEXT NULL,"
          + ShotQueueTable.EVENT_TITLE+ " TEXT NULL,"
          + ShotTable.TYPE+ " TEXT NOT NULL,"
          + ShotTable.ID_SHOT_PARENT+ " TEXT NULL,"
          + ShotTable.ID_USER_PARENT+ " TEXT NULL,"
          + ShotTable.USERNAME_PARENT+ " TEXT NULL,"
          + ShotTable.VIDEO_URL+ " TEXT NULL,"
          + ShotTable.VIDEO_TITLE+ " TEXT NULL,"
          + ShotTable.VIDEO_DURATION+ " NUMBER NULL,"
          + SyncColumns.BIRTH + " DATETIME NOT NULL,"
          + SyncColumns.MODIFIED + " DATETIME NOT NULL,"
          + SyncColumns.DELETED + " DATETIME NULL,"
          + SyncColumns.REVISION + " INT NOT NULL,"
          + SyncColumns.SYNCHRONIZED + " CHAR(1) NULL" +
          ")";

    public static final String CREATE_TABLE_FOLLOW = "CREATE TABLE IF NOT EXISTS " + FollowTable.TABLE + " ("
            + FollowTable.ID_USER + " TEXT NOT NULL,"
            + FollowTable.ID_FOLLOWED_USER + " TEXT NOT NULL,"
            + SyncColumns.BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.DELETED + " DATETIME NULL,"
            + SyncColumns.REVISION + " INT NOT NULL,"
            + SyncColumns.SYNCHRONIZED + " CHAR(1) NULL,"
            + " PRIMARY KEY(" + FollowTable.ID_USER + "," + FollowTable.ID_FOLLOWED_USER + "))";

    public static final String CREATE_TABLE_TABLESSYNC = "CREATE TABLE IF NOT EXISTS "+ TablesSync.TABLE+" ("
            + TablesSync.MIN_TIMESTAMP+" DATETIME,"
            + TablesSync.MAX_TIMESTAMP+" DATETIME,"
            + TablesSync.MIN_ROWS+" INT,"
            + TablesSync.MAX_ROWS+" INT,"
            + TablesSync.DIRECTION+" VARCHAR(255) NULL,"
            + TablesSync.ORDER +" INT NOT NULL,"
            + TablesSync.ENTITY+" VARCHAR(255) NOT NULL,"
            + TablesSync.FREQUENCY+" INT NULL, " +
            "PRIMARY KEY("+TablesSync.ORDER+"));";

    public static final String CREATE_TABLE_DEVICE = "CREATE TABLE IF NOT EXISTS "+ DeviceTable.TABLE +" ("
            + DeviceTable.ID_DEVICE+" TEXT NULL,"
            + DeviceTable.ID_USER+" TEXT NOT NULL PRIMARY KEY,"
            + DeviceTable.PLATFORM +" TEXT NOT NULL,"
            + DeviceTable.TOKEN+" VARCHAR(255) NULL,"
            + DeviceTable.UNIQUE_DEVICE_ID+" VARCHAR(255) NULL,"
            + DeviceTable.MODEL+" VARCHAR(255) NULL,"
            + DeviceTable.OS_VERSION+" VARCHAR(255),"
            + SyncColumns.BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.DELETED + " DATETIME NULL,"
            + SyncColumns.REVISION + " INT NOT NULL,"
            + SyncColumns.SYNCHRONIZED + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS "+ DatabaseContract.EventTable.TABLE+" ("
            + DatabaseContract.EventTable.ID_EVENT +" TEXT NOT NULL PRIMARY KEY,"
            + DatabaseContract.EventTable.ID_USER +" TEXT NOT NULL,"
            + DatabaseContract.EventTable.ID_USER_EVENT +" TEXT,"
            + DatabaseContract.EventTable.USERNAME +" TEXT NOT NULL,"
            + DatabaseContract.EventTable.TITLE +" VARCHAR(255) NOT NULL,"
            + DatabaseContract.EventTable.PHOTO +" TEXT NULL,"
            + DatabaseContract.EventTable.TAG +" TEXT NULL,"
            + DatabaseContract.EventTable.LAST_UPDATED_USER +" TEXT NULL,"
            + DatabaseContract.EventTable.LOCALE +" TEXT NULL,"
            + SyncColumns.BIRTH +" DATETIME NOT NULL,"
            + SyncColumns.MODIFIED +" DATETIME NOT NULL,"
            + SyncColumns.DELETED +" DATETIME NULL,"
            + SyncColumns.REVISION +" INT NOT NULL,"
            + SyncColumns.SYNCHRONIZED +" CHAR(1) NULL)";

    public static final String CREATE_TABLE_EVENT_SEARCH = "CREATE TABLE IF NOT EXISTS "+ DatabaseContract.EventSearchTable.TABLE+" ("
            + DatabaseContract.EventTable.ID_EVENT +" TEXT NOT NULL PRIMARY KEY,"
            + DatabaseContract.EventTable.ID_USER +" TEXT NOT NULL,"
            + DatabaseContract.EventTable.USERNAME +" TEXT NOT NULL,"
            + DatabaseContract.EventTable.TITLE +" VARCHAR(255) NOT NULL,"
            + DatabaseContract.EventTable.PHOTO +" TEXT NULL,"
            + DatabaseContract.EventTable.LOCALE +" TEXT NULL,"
            + DatabaseContract.EventTable.TAG +" TEXT NULL,"
            + SyncColumns.BIRTH
      +" DATETIME NOT NULL,"
            + SyncColumns.MODIFIED
      +" DATETIME NOT NULL,"
            + SyncColumns.DELETED
      +" DATETIME NULL,"
            + SyncColumns.REVISION
      +" INT NOT NULL,"
            + SyncColumns.SYNCHRONIZED
      +" CHAR(1) NULL,"
            + DatabaseContract.EventSearchTable.WATCHERS +" INT NOT NULL)";

    public static final String CREATE_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS " + DatabaseContract.FavoriteTable.TABLE + " ("
      + DatabaseContract.FavoriteTable.ID_EVENT + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.FavoriteTable.ORDER + " NUMBER NULL,"
      + SyncColumns.SYNCHRONIZED +" TEXT NULL)";


    public static final String CREATE_TABLE_ACTIVITY = "CREATE TABLE IF NOT EXISTS " + DatabaseContract.ActivityTable.TABLE + " ("
      + DatabaseContract.ActivityTable.ID_ACTIVITY + " TEXT NOT NULL PRIMARY KEY,"
      + DatabaseContract.ActivityTable.ID_USER + " TEXT NOT NULL,"
      + DatabaseContract.ActivityTable.USERNAME + " TEXT NOT NULL,"
      + DatabaseContract.ActivityTable.ID_EVENT + " TEXT NULL,"
      + DatabaseContract.ActivityTable.USER_PHOTO + " TEXT NULL,"
      + DatabaseContract.ActivityTable.EVENT_TAG + " VARCHAR(255) NULL,"
      + DatabaseContract.ActivityTable.EVENT_TITLE + " VARCHAR(255) NULL,"
      + DatabaseContract.ActivityTable.COMMENT+ " VARCHAR(255) NULL,"
      + DatabaseContract.ActivityTable.TYPE+ " TEXT NULL,"
      + SyncColumns.BIRTH + " DATETIME NOT NULL,"
      + SyncColumns.MODIFIED + " DATETIME NOT NULL,"
      + SyncColumns.DELETED + " DATETIME NULL,"
      + SyncColumns.REVISION + " INT NOT NULL,"
      + SyncColumns.SYNCHRONIZED + " CHAR(1) NULL)";

}
