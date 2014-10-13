package gm.mobi.android.db;

import gm.mobi.android.db.GMContract.FollowTable;
import gm.mobi.android.db.GMContract.ShotTable;
import gm.mobi.android.db.GMContract.SyncColumns;
import gm.mobi.android.db.GMContract.TablesSync;
import gm.mobi.android.db.GMContract.TeamTable;
import gm.mobi.android.db.GMContract.UserTable;


public class SQLiteUtils {

    public static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + UserTable.TABLE + " ("
            + UserTable.ID + " INT NOT NULL PRIMARY KEY,"
            + UserTable.FAVORITE_TEAM_ID + " INT NOT NULL,"
            + UserTable.FAVORITE_TEAM_NAME+" VARCHAR(255),"
            + UserTable.SESSION_TOKEN + " VARCHAR(255),"
            + UserTable.USER_NAME + " VARCHAR(255),"
            + UserTable.EMAIL + " VARCHAR(255),"
            + UserTable.NAME + " VARCHAR(255),"
            + UserTable.PHOTO + " VARCHAR(1024) NOT NULL,"
            + UserTable.POINTS+" INT NOT NULL,"
            + UserTable.NUM_FOLLOWINGS+" INT NOT NULL,"
            + UserTable.NUM_FOLLOWERS+" INT NOT NULL,"
            + UserTable.WEBSITE+" VARCHAR(255),"
            + UserTable.RANK+" INT,"
            + UserTable.BIO+" VARCHAR(150),"
            + UserTable.NAME_NORMALIZED+" VARCHAR(255),"
            + UserTable.USER_NAME_NORMALIZED+" VARCHAR(255),"
            + UserTable.EMAIL_NORMALIZED+" VARCHAR(255) NULL,"
            + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED + " DATETIME,"
            + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1));";

    public static final String CREATE_TABLE_SHOT = "CREATE TABLE IF NOT EXISTS " + ShotTable.TABLE + " ("
            + ShotTable.ID_SHOT + " INT NOT NULL,"
            + ShotTable.ID_USER + " INT NOT NULL,"
            + ShotTable.COMMENT + " VARCHAR(255) NOT NULL,"
            + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED + " DATETIME NULL,"
            + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1) NULL," +
            " PRIMARY KEY(" + ShotTable.ID_SHOT + "))";

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

    public static final String CREATE_TABLE_TEAM = "CREATE TABLE IF NOT EXISTS "+TeamTable.TABLE+" ("
            + TeamTable.ID_TEAM+" INT NOT NULL PRIMARY KEY,"
            + TeamTable.OFFICIAL_NAME+" VARCHAR(255),"
            + TeamTable.CLUB_NAME+" VARCHAR(255),"
            + TeamTable.SHORT_NAME+" VARCHAR(255),"
            + TeamTable.TLA_NAME+" VARCHAR(255),"
            + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED + " DATETIME NULL,"
            + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1) NULL)";
}
