package gm.mobi.android.db;


import gm.mobi.android.db.GMContract.*;


public class SQLiteUtils {

   public static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + UserTable.TABLE+ " ("
           + UserTable.ID + " INT NOT NULL PRIMARY KEY,"
           + UserTable.FAVOURITE_TEAM_ID + " INT NOT NULL,"
           + UserTable.SESSION_TOKEN+ " VARCHAR(255) NULL,"
           + UserTable.USER_NAME+ " VARCHAR(255) NULL,"
           + UserTable.EMAIL+ " VARCHAR(255) NULL,"
           + UserTable.NAME+ " VARCHAR(255) NULL,"
           + UserTable.PHOTO+ " VARCHAR(1024) NOT NULL,"
           + SyncColumns.CSYS_BIRTH + " DATETIME NOT NULL,"
           + SyncColumns.CSYS_MODIFIED + " DATETIME NOT NULL,"
           + SyncColumns.CSYS_DELETED + " DATETIME NULL,"
           + SyncColumns.CSYS_REVISION + " INT NOT NULL,"
           + SyncColumns.CSYS_SYNCHRONIZED + " CHAR(1) NULL);";

    public static final String CREATE_TABLE_SHOT = "CREATE TABLE IF NOT EXISTS "+ShotTable.TABLE+" ("
            + ShotTable.ID_SHOT+" INT NOT NULL,"
            + ShotTable.ID_USER+" INT NOT NULL,"
            + ShotTable.COMMENT+" VARCHAR(255) NOT NULL,"
            + SyncColumns.CSYS_BIRTH+" DATETIME NOT NULL,"
            + SyncColumns.CSYS_MODIFIED+" DATETIME NOT NULL,"
            + SyncColumns.CSYS_DELETED+" DATETIME NULL,"
            + SyncColumns.CSYS_REVISION+" INT NOT NULL,"
            + SyncColumns.CSYS_SYNCHRONIZED+" CHAR(1) NULL," +
            " PRIMARY KEY("+ShotTable.ID_SHOT+"), " +
            " CONSTRAINT 'FK_SHOT_USER1' FOREIGN KEY("+ShotTable.ID_USER+") REFERENCES "+UserTable.TABLE+"("+ShotTable.ID_USER+") ON DELETE NO ACTION ON UPDATE NO ACTION );";
}
