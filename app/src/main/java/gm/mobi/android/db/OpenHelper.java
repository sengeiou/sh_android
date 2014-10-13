package gm.mobi.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bagdad.db";
    public static final int DATABASE_VERSION = 1;

    public OpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public OpenHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteUtils.CREATE_TABLE_USER);
        db.execSQL(SQLiteUtils.CREATE_TABLE_SHOT);
        db.execSQL(SQLiteUtils.CREATE_TABLE_FOLLOW);
        db.execSQL(SQLiteUtils.CREATE_TABLE_TABLESSYNC);
        db.execSQL(SQLiteUtils.CREATE_TABLE_TEAM);
        db.execSQL(SQLiteUtils.CREATE_TABLE_DEVICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GMContract.UserTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GMContract.ShotTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GMContract.FollowTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GMContract.TablesSync.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GMContract.TeamTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GMContract.DeviceTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
