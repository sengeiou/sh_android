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
import com.shootr.android.util.TimeUtils;
import javax.inject.Inject;
import timber.log.Timber;

public abstract class AbstractManager {

    public static int NUMDAYS = 60;
    @Inject protected SQLiteOpenHelper dbHelper;

    SQLiteDatabase db;


    public void setDataBase(SQLiteDatabase db){
        this.db = db;
    }

    public  void deleteDatabase(Context context) {
        context.deleteDatabase(ShootrDbOpenHelper.DATABASE_NAME);
        Timber.d("Database deleted");
    }

    public  boolean isTableEmpty( String entity) {
        boolean res = true;
        String raw_query = "SELECT * FROM "+ entity;
        Cursor c = db.rawQuery(raw_query, new String[]{});
        if (c.getCount() > 0) res = false;
        c.close();

        return res;
    }

    /**
     * Retrieve row's number for table we need
     * */

    public  long numberOfRows( String table){
        long numRows = DatabaseUtils.queryNumEntries(db, table);
        return numRows;
    }

    public long insertOrUpdateSyncTable(TableSync tableSync){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.TablesSync.ORDER,tableSync.getOrder());
        contentValues.put(DatabaseContract.TablesSync.ENTITY, tableSync.getEntity());
        contentValues.put(DatabaseContract.TablesSync.DIRECTION, tableSync.getDirection());
        contentValues.put(DatabaseContract.TablesSync.FREQUENCY, tableSync.getFrequency());
        contentValues.put(DatabaseContract.TablesSync.MAX_ROWS, tableSync.getMaxRows());
        contentValues.put(DatabaseContract.TablesSync.MIN_ROWS, tableSync.getMinRows());
        contentValues.put(DatabaseContract.TablesSync.MIN_TIMESTAMP, tableSync.getMin_timestamp());
        contentValues.put(DatabaseContract.TablesSync.MAX_TIMESTAMP, tableSync.getMax_timestamp());
        return db.insertWithOnConflict(DatabaseContract.TablesSync.TABLE, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int getNumMaxOfRowsByEntity(String entity){
        int numRows = 0;

        String[] column = new String[]{DatabaseContract.TablesSync.MAX_ROWS};
        String[] stringArgs = new String[]{entity};
        String args = DatabaseContract.TablesSync.ENTITY+"=?";
        Cursor c = db.query(DatabaseContract.TablesSync.TABLE, column,args,stringArgs,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            numRows = c.getInt(0);
        }
        c.close();
        return numRows;
    }



    public int deleteRows(long number){
        String sql = "SELECT min(idShot) as idShot FROM (SELECT "+ DatabaseContract.ShotTable.ID_SHOT +" FROM "+ DatabaseContract.ShotTable.TABLE+" order by "+ DatabaseContract.ShotTable.ID_SHOT+" DESC LIMIT 10)";
        int idShot = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            idShot = c.getInt(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT));
        }
        c.close();
        return db.delete(DatabaseContract.ShotTable.TABLE, DatabaseContract.ShotTable.ID_SHOT+"<"+idShot,null);
    }

    public  Long getFirstModifiedDate(String entity){
        Long firstDateModified;
        if(isTableEmpty(entity)){
            firstDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
        }else{
            String sql = "SELECT "+ DatabaseContract.SyncColumns.CSYS_MODIFIED+ " FROM "+entity+" ORDER BY " + DatabaseContract.SyncColumns.CSYS_MODIFIED+" ASC LIMIT 1";
            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                firstDateModified = c.getLong(c.getColumnIndex(DatabaseContract.SyncColumns.CSYS_MODIFIED));
            } else {
                firstDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
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
            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                lastDateModified = c.getLong(c.getColumnIndex(DatabaseContract.SyncColumns.CSYS_MODIFIED));
            } else {
                lastDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
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
        tablesSync.setMax_timestamp(System.currentTimeMillis());

        if(isTableEmpty(tableName)){
            tablesSync.setMin_timestamp(System.currentTimeMillis());
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
