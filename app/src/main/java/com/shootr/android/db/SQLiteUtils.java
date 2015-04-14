package com.shootr.android.db;

import com.shootr.android.db.DatabaseContract.DeviceTable;
import com.shootr.android.db.DatabaseContract.FollowTable;
import com.shootr.android.db.DatabaseContract.ShotQueueTable;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.DatabaseContract.SyncColumns;
import com.shootr.android.db.DatabaseContract.TablesSync;
import com.shootr.android.db.DatabaseContract.TeamTable;
import com.shootr.android.db.DatabaseContract.UserTable;

public class SQLiteUtils {

    private SQLiteUtils() {
    }

    public static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + UserTable.TABLE + " ("
            + UserTable.ID + " INT NOT NULL PRIMARY KEY,"
            + UserTable.FAVORITE_TEAM_ID + " INT NULL,"
            + UserTable.FAVORITE_TEAM_NAME+" VARCHAR(255),"
            + UserTable.SESSION_TOKEN + " VARCHAR(255),"
            + UserTable.USER_NAME + " VARCHAR(255),"
            + UserTable.EMAIL + " VARCHAR(255),"
            + UserTable.NAME + " VARCHAR(255),"
            + UserTable.PHOTO + " VARCHAR(1024) NULL,"
            + UserTable.POINTS+" INT NOT NULL,"
            + UserTable.NUM_FOLLOWINGS+" INT NOT NULL,"
            + UserTable.NUM_FOLLOWERS+" INT NOT NULL,"
            + UserTable.WEBSITE+" VARCHAR(255),"
            + UserTable.RANK+" INT,"
            + UserTable.BIO+" VARCHAR(150),"
            + UserTable.NAME_NORMALIZED+" VARCHAR(255),"
            + UserTable.USER_NAME_NORMALIZED+" VARCHAR(255),"
            + UserTable.EMAIL_NORMALIZED+" VARCHAR(255) NULL,"
            + UserTable.EVENT_ID+" INT NULL,"
            + UserTable.EVENT_TITLE+" TEXT NULL,"
            + UserTable.STATUS+" TEXT NULL,"
            + UserTable.CHECK_IN+" INT NULL,"
            + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED + " DATETIME,"
            + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1));";

    public static final String CREATE_TABLE_SHOT = "CREATE TABLE IF NOT EXISTS " + ShotTable.TABLE + " ("
            + ShotTable.ID_SHOT + " INT NOT NULL PRIMARY KEY,"
            + ShotTable.ID_USER + " INT NOT NULL,"
            + ShotTable.COMMENT + " VARCHAR(255) NULL,"
            + ShotTable.IMAGE + " VARCHAR(255) NULL,"
            + ShotTable.ID_EVENT+ " INT NULL,"
            + ShotTable.EVENT_TAG+ " TEXT NULL,"
            + ShotTable.EVENT_TITLE+ " TEXT NULL,"
            + ShotTable.TYPE+ " INT NOT NULL,"
            + ShotTable.ID_SHOT_PARENT+ " INT NULL,"
            + ShotTable.ID_USER_PARENT+ " INT NULL,"
            + ShotTable.USERNAME_PARENT+ " TEXT NULL,"
            + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED + " DATETIME NULL,"
            + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_SHOT_QUEUE = "CREATE TABLE IF NOT EXISTS " + ShotQueueTable.TABLE + " ("
          + ShotQueueTable.ID_QUEUE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + ShotQueueTable.FAILED + " INT NOT NULL,"
          + ShotQueueTable.IMAGE_FILE+ " TEXT NULL,"
          + ShotQueueTable.ID_SHOT + " INT NULL,"
          + ShotQueueTable.ID_USER + " INT NOT NULL,"
          + ShotQueueTable.COMMENT + " VARCHAR(255) NULL,"
          + ShotQueueTable.IMAGE + " VARCHAR(255) NULL,"
          + ShotQueueTable.ID_EVENT+ " INT NULL,"
          + ShotQueueTable.EVENT_TAG+ " TEXT NULL,"
          + ShotQueueTable.EVENT_TITLE+ " TEXT NULL,"
          + ShotQueueTable.TYPE+ " INT NOT NULL,"
          + ShotTable.ID_SHOT_PARENT+ " INT NULL,"
          + ShotTable.ID_USER_PARENT+ " INT NULL,"
          + ShotTable.USERNAME_PARENT+ " TEXT NULL,"
          + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
          + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
          + SyncColumns.CSYS_DELETED + " DATETIME NULL,"
          + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
          + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1) NULL" +
          ")";

    public static final String CREATE_TABLE_FOLLOW = "CREATE TABLE IF NOT EXISTS " + FollowTable.TABLE + " ("
            + FollowTable.ID_USER + " INT NOT NULL,"
            + FollowTable.ID_FOLLOWED_USER + " INT NOT NULL,"
            + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED + " DATETIME NULL,"
            + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1) NULL,"
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
            + DeviceTable.ID_DEVICE+" INT NOT NULL PRIMARY KEY,"
            + DeviceTable.ID_USER+" INT NOT NULL,"
            + DeviceTable.TOKEN+" VARCHAR(255) NULL,"
            + DeviceTable.UNIQUE_DEVICE_ID+" VARCHAR(255) NULL,"
            + DeviceTable.MODEL+" VARCHAR(255) NULL,"
            + DeviceTable.OS_VERSION+" VARCHAR(255),"
            + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED + " DATETIME NULL,"
            + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1) NULL)";

    public static final String CREATE_TABLE_TEAM = "CREATE TABLE IF NOT EXISTS "+ TeamTable.TABLE+" ("
            + TeamTable.ID_TEAM+" INT NOT NULL PRIMARY KEY,"
            + TeamTable.CLUB_NAME+" VARCHAR(255) NOT NULL,"
            + TeamTable.OFFICIAL_NAME+" VARCHAR(255),"
            + TeamTable.SHORT_NAME+" VARCHAR(255),"
            + TeamTable.TLA_NAME+" VARCHAR(255),"
            + SyncColumns.CSYS_BIRTH+" DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED+ "DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";

    public static final String CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS "+ DatabaseContract.EventTable.TABLE+" ("
            + DatabaseContract.EventTable.ID_EVENT +" INT NOT NULL PRIMARY KEY,"
            + DatabaseContract.EventTable.ID_USER +" INT NOT NULL,"
            + DatabaseContract.EventTable.USERNAME +" TEXT NOT NULL,"
            + DatabaseContract.EventTable.BEGIN_DATE + " DATETIME NOT NULL,"
            + DatabaseContract.EventTable.END_DATE + " DATETIME NOT NULL,"
            + DatabaseContract.EventTable.ID_LOCAL_TEAM+" INT NULL,"
            + DatabaseContract.EventTable.ID_VISITOR_TEAM+" INT NULL,"
            + DatabaseContract.EventTable.TITLE +" VARCHAR(255) NOT NULL,"
            + DatabaseContract.EventTable.PHOTO +" TEXT NULL,"
            + DatabaseContract.EventTable.TIMEZONE +" TEXT NOT NULL,"
            + DatabaseContract.EventTable.TAG +" TEXT NULL,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL)";
    }
