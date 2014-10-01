package gm.mobi.android.db.manager;

import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import gm.mobi.android.constant.SyncConstants;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.TablesSync;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.util.TimeUtils;


public class SyncTableManager {

    public static int NUMDAYS = 15;
    
    public static Long getLastModifiedDate(SQLiteDatabase db, String entity) {
        Long lastDateModified;
        if(GeneralManager.isTableEmpty(db,entity)){
            lastDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
        }else{
            String sql = "SELECT "+ GMContract.SyncColumns.CSYS_MODIFIED+ " FROM "+entity+" ORDER BY " + GMContract.SyncColumns.CSYS_MODIFIED+" DESC LIMIT 1";
            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                lastDateModified = c.getLong(c.getColumnIndex(GMContract.SyncColumns.CSYS_MODIFIED));
            } else {
                lastDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
            }
            c.close();
        }
        return lastDateModified;
    }

    public static Long getFirstModifiedDate(SQLiteDatabase db, String entity){
        Long firstDateModified;
        if(GeneralManager.isTableEmpty(db,entity)){
            firstDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
        }else{
           String sql = "SELECT "+ GMContract.SyncColumns.CSYS_MODIFIED+ " FROM "+entity+" ORDER BY " + GMContract.SyncColumns.CSYS_MODIFIED+" ASC LIMIT 1";
            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                firstDateModified = c.getLong(c.getColumnIndex(GMContract.SyncColumns.CSYS_MODIFIED));
            } else {
                firstDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
            }
        }
        return firstDateModified;
    }
    public static long insertOrUpdateSyncTable(SQLiteDatabase db, TableSync tableSync){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TablesSync.ORDER,tableSync.getOrder());
        contentValues.put(TablesSync.ENTITY, tableSync.getEntity());
        contentValues.put(TablesSync.DIRECTION, tableSync.getDirection());
        contentValues.put(TablesSync.FREQUENCY, tableSync.getFrequency());
        contentValues.put(TablesSync.MAX_ROWS, tableSync.getMaxRows());
        contentValues.put(TablesSync.MIN_ROWS, tableSync.getMinRows());
        contentValues.put(TablesSync.MIN_TIMESTAMP, tableSync.getMin_timestamp());
        contentValues.put(TablesSync.MAX_TIMESTAMP, tableSync.getMax_timestamp());
        return db.insertWithOnConflict(TablesSync.TABLE, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static int getNumMaxOfRowsByEntity(SQLiteDatabase db, String entity){
        int numRows = 0;

        String[] column = new String[]{TablesSync.MAX_ROWS};
        String[] stringArgs = new String[]{entity};

        String args = TablesSync.ENTITY+"=?";

        Cursor c = db.query(TablesSync.TABLE, column,args,stringArgs,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            numRows = c.getInt(0);
        }

        c.close();
        return numRows;
    }

}
