package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.ShootrDbOpenHelper;
import com.shootr.android.db.objects.TableSync;
import com.shootr.android.util.AndroidTimeUtils;
import javax.inject.Inject;
import timber.log.Timber;

public abstract class AbstractManager {

    public static final int NUMDAYS = 60;
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

    public  boolean isTableEmpty( String entity) {
        boolean res = true;
        String raw_query = "SELECT * FROM "+ entity;
        Cursor c = getReadableDatabase().rawQuery(raw_query, new String[] { });
        if (c.getCount() > 0) {
            res = false;
        }
        c.close();

        return res;
    }

    public long insertOrUpdateSyncTable(TableSync tableSync){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.TablesSync.ORDER,tableSync.getOrder());
        contentValues.put(DatabaseContract.TablesSync.ENTITY, tableSync.getEntity());
        contentValues.put(DatabaseContract.TablesSync.DIRECTION, tableSync.getDirection());
        contentValues.put(DatabaseContract.TablesSync.FREQUENCY, tableSync.getFrequency());
        contentValues.put(DatabaseContract.TablesSync.MAX_ROWS, tableSync.getMaxRows());
        contentValues.put(DatabaseContract.TablesSync.MIN_ROWS, tableSync.getMinRows());
        contentValues.put(DatabaseContract.TablesSync.MIN_TIMESTAMP, tableSync.getMinTimestamp());
        contentValues.put(DatabaseContract.TablesSync.MAX_TIMESTAMP, tableSync.getMaxTimestamp());
        return getWritableDatabase().insertWithOnConflict(DatabaseContract.TablesSync.TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }

    public  Long getFirstModifiedDate(String entity){
        Long firstDateModified;
        if(isTableEmpty(entity)){
            firstDateModified = AndroidTimeUtils.getNDaysAgo(NUMDAYS);
        }else{
            String sql = "SELECT "+ DatabaseContract.SyncColumns.CSYS_MODIFIED+ " FROM "+entity+" ORDER BY " + DatabaseContract.SyncColumns.CSYS_MODIFIED+" ASC LIMIT 1";
            Cursor c = getReadableDatabase().rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                firstDateModified = c.getLong(c.getColumnIndex(DatabaseContract.SyncColumns.CSYS_MODIFIED));
            } else {
                firstDateModified = AndroidTimeUtils.getNDaysAgo(NUMDAYS);
            }
            c.close();
        }
        return firstDateModified;
    }

    public Long getLastModifiedDate(String entity) {
        Long lastDateModified;
        if(isTableEmpty(entity)){
            lastDateModified = 0L;
        }else{
            String sql = "SELECT "+ DatabaseContract.SyncColumns.CSYS_MODIFIED+ " FROM "+entity+" ORDER BY " + DatabaseContract.SyncColumns.CSYS_MODIFIED+" DESC LIMIT 1";
            Cursor c = getReadableDatabase().rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                lastDateModified = c.getLong(c.getColumnIndex(DatabaseContract.SyncColumns.CSYS_MODIFIED));
            } else {
                lastDateModified = AndroidTimeUtils.getNDaysAgo(NUMDAYS);
            }
            c.close();
        }
        return lastDateModified;
    }


    public long insertInTableSync(String tableName, int order, int maxRows, int minRows){
        TableSync tablesSync = new TableSync();
        tablesSync.setOrder(order); // It's the second data type the application insert in database
        tablesSync.setDirection("BOTH");
        tablesSync.setEntity(tableName);
        tablesSync.setMaxTimestamp(System.currentTimeMillis());

        if(isTableEmpty(tableName)){
            tablesSync.setMinTimestamp(System.currentTimeMillis());
        }
        tablesSync.setMaxRows(maxRows);
        tablesSync.setMinRows(minRows);
        return insertOrUpdateSyncTable(tablesSync);
    }
    public String createListPlaceholders(int length) {
        if (length < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("At least one placeholder must be created, otherwise this method is useless.");
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
