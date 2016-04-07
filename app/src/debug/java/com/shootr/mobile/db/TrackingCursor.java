package com.shootr.mobile.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteQuery;

import java.util.LinkedList;
import java.util.List;

public class TrackingCursor extends SQLiteCursor {

    private static final List<Cursor> openCursors = new LinkedList<Cursor>();
    private StackTraceElement[] stackTrace;

    public TrackingCursor(SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        super(driver, editTable, query);
        synchronized (openCursors){
            openCursors.add(this);
            stackTrace = Thread.currentThread().getStackTrace();
        }

    }

    public void close() {
        super.close();
        synchronized (openCursors) {
            openCursors.remove(this);
        }
    }

    public static List<Cursor> getOpenCursors() {
        return openCursors;
    }
}