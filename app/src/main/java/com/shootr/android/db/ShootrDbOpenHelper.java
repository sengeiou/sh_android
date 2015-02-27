package com.shootr.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShootrDbOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shootr.db";
    public static final int DATABASE_VERSION = 2;

    public ShootrDbOpenHelper(Context context, SQLiteDatabase.CursorFactory cursorFactory) {
        super(context, DATABASE_NAME, cursorFactory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteUtils.CREATE_TABLE_USER);
        db.execSQL(SQLiteUtils.CREATE_TABLE_SHOT);
        db.execSQL(SQLiteUtils.CREATE_TABLE_FOLLOW);
        db.execSQL(SQLiteUtils.CREATE_TABLE_TABLESSYNC);
        db.execSQL(SQLiteUtils.CREATE_TABLE_DEVICE);
        db.execSQL(SQLiteUtils.CREATE_TABLE_TEAM);
        db.execSQL(SQLiteUtils.CREATE_TABLE_EVENT);
        db.execSQL(SQLiteUtils.CREATE_TABLE_WATCH);
        db.execSQL(SQLiteUtils.CREATE_TABLE_SHOT_QUEUE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ShotTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.FollowTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TablesSync.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DeviceTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TeamTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.EventTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.WatchTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ShotQueueTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
