package com.shootr.mobile.db.manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import javax.inject.Inject;

public abstract class AbstractManager {

    @Inject protected SQLiteOpenHelper dbHelper;

    protected AbstractManager(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    protected SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }

    protected SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase();
    }

    public boolean isTableEmpty(String entity) {
        boolean res = true;
        String raw_query = "SELECT * FROM " + entity;
        Cursor c = getReadableDatabase().rawQuery(raw_query, new String[] {});
        if (c.getCount() > 0) {
            res = false;
        }
        c.close();

        return res;
    }

    public String createListPlaceholders(int length) {
        if (length < 1) {
            // It will lead to an invalid query anyway ..
            throw new IllegalArgumentException(
              "At least one placeholder must be created, otherwise this method is useless.");
        } else {
            StringBuilder sb = new StringBuilder(length * 2 - 1);
            sb.append("?");
            for (int i = 1; i < length; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

}
