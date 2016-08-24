package com.shootr.mobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.util.Version;

public class ShootrDbOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shootr.db";

    public ShootrDbOpenHelper(Context context, SQLiteDatabase.CursorFactory cursorFactory, Version version) {
        super(context, DATABASE_NAME, cursorFactory, version.getDatabaseVersion());
    }

    public ShootrDbOpenHelper(Context context, Version version) {
        super(context, DATABASE_NAME, null, version.getDatabaseVersion());
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteUtils.CREATE_TABLE_USER);
        db.execSQL(SQLiteUtils.CREATE_TABLE_SHOT);
        db.execSQL(SQLiteUtils.CREATE_TABLE_FOLLOW);
        db.execSQL(SQLiteUtils.CREATE_TABLE_BLOCK);
        db.execSQL(SQLiteUtils.CREATE_TABLE_BAN);
        db.execSQL(SQLiteUtils.CREATE_TABLE_MUTE);
        db.execSQL(SQLiteUtils.CREATE_TABLE_DEVICE);
        db.execSQL(SQLiteUtils.CREATE_TABLE_STREAM);
        db.execSQL(SQLiteUtils.CREATE_TABLE_SHOT_QUEUE);
        db.execSQL(SQLiteUtils.CREATE_TABLE_STREAM_SEARCH);
        db.execSQL(SQLiteUtils.CREATE_TABLE_FAVORITE);
        db.execSQL(SQLiteUtils.CREATE_TABLE_ACTIVITY);
        db.execSQL(SQLiteUtils.CREATE_SUGGESTED_PEOPLE);
        db.execSQL(SQLiteUtils.CREATE_TABLE_NICE_SHOTS);
        db.execSQL(SQLiteUtils.CREATE_TABLE_CONTRIBUTOR);
        db.execSQL(SQLiteUtils.CREATE_TABLE_TIMELINE_SYNC);
        db.execSQL(SQLiteUtils.CREATE_TABLE_POLL);
        db.execSQL(SQLiteUtils.CREATE_TABLE_POLL_OPTION);
        db.execSQL(SQLiteUtils.CREATE_TABLE_HIGHTLIGHTED_SHOT);
        db.execSQL(SQLiteUtils.CREATE_TABLE_RECENT_STREAM);
        db.execSQL(SQLiteUtils.CREATE_TABLE_SHOT_EVENT);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ShotTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.FollowTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DeviceTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.StreamTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ShotQueueTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.StreamSearchTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ActivityTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SuggestedPeopleTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.NiceShotTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TimelineSyncTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.BlockTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.BanTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MuteTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ContributorTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.PollTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.PollOptionTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.HighlightedShotTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.RecentStreamTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ShotEventTable.TABLE);
        onCreate(db);
    }

    @Override public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
