package com.shootr.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.util.Version;

public class ShootrDbOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shootr.db";

    public ShootrDbOpenHelper(Context context, SQLiteDatabase.CursorFactory cursorFactory, Version version) {
        super(context, DATABASE_NAME, cursorFactory, version.getDatabaseVersion());
    }

    public ShootrDbOpenHelper(Context context, Version version) {
        super(context, DATABASE_NAME, null, version.getDatabaseVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteUtils.CREATE_TABLE_USER);
        db.execSQL(SQLiteUtils.CREATE_TABLE_SHOT);
        db.execSQL(SQLiteUtils.CREATE_TABLE_FOLLOW);
        db.execSQL(SQLiteUtils.CREATE_TABLE_TABLESSYNC);
        db.execSQL(SQLiteUtils.CREATE_TABLE_DEVICE);
        db.execSQL(SQLiteUtils.CREATE_TABLE_EVENT);
        db.execSQL(SQLiteUtils.CREATE_TABLE_SHOT_QUEUE);
        db.execSQL(SQLiteUtils.CREATE_TABLE_EVENT_SEARCH);
        db.execSQL(SQLiteUtils.CREATE_TABLE_FAVORITE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ShotTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.FollowTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TablesSync.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DeviceTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.EventTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ShotQueueTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.EventSearchTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
