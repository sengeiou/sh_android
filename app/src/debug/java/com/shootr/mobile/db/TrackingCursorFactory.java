package com.shootr.mobile.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class TrackingCursorFactory implements SQLiteDatabase.CursorFactory {
    public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
        return new TrackingCursor(masterQuery, editTable, query);
    }
}