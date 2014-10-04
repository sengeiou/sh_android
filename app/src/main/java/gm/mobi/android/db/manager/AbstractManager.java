package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.OpenHelper;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.util.TimeUtils;
import timber.log.Timber;

public abstract class AbstractManager {

    public static int NUMDAYS = 30;
    protected SQLiteDatabase db;


    public void setDataBase(SQLiteDatabase db){
        this.db = db;
    }

    public  void deleteDatabase(Context context) {
        context.deleteDatabase(OpenHelper.DATABASE_NAME);
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

    public  long insertOrUpdate( String tableName,ContentValues contentValues,String[] projection,String where, String[] args){
        Cursor c;
        long res = 0;
        try{
            c = db.query(tableName,projection,where, args, null, null, null );
            if(c.getCount()>0){
                res = db.update(tableName,contentValues,where,args);
            }else{
                res = db.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

        }catch(Exception e){
            Timber.e("[UPDATE EXCEPTION], insertOrUpdate in table %s with exception %s and message %s",tableName,e, e.getMessage());
        }
        return res;
    }

    public long insertOrUpdateSyncTable(TableSync tableSync){
        ContentValues contentValues = new ContentValues();
        contentValues.put(GMContract.TablesSync.ORDER,tableSync.getOrder());
        contentValues.put(GMContract.TablesSync.ENTITY, tableSync.getEntity());
        contentValues.put(GMContract.TablesSync.DIRECTION, tableSync.getDirection());
        contentValues.put(GMContract.TablesSync.FREQUENCY, tableSync.getFrequency());
        contentValues.put(GMContract.TablesSync.MAX_ROWS, tableSync.getMaxRows());
        contentValues.put(GMContract.TablesSync.MIN_ROWS, tableSync.getMinRows());
        contentValues.put(GMContract.TablesSync.MIN_TIMESTAMP, tableSync.getMin_timestamp());
        contentValues.put(GMContract.TablesSync.MAX_TIMESTAMP, tableSync.getMax_timestamp());
        return db.insertWithOnConflict(GMContract.TablesSync.TABLE, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int getNumMaxOfRowsByEntity(String entity){
        int numRows = 0;

        String[] column = new String[]{GMContract.TablesSync.MAX_ROWS};
        String[] stringArgs = new String[]{entity};

        String args = GMContract.TablesSync.ENTITY+"=?";

        Cursor c = db.query(GMContract.TablesSync.TABLE, column,args,stringArgs,null,null,null,null);
        if(c.getCount()>0){
            c.moveToFirst();
            numRows = c.getInt(0);
        }

        c.close();
        return numRows;
    }



    public int deleteRows(long number){
        String sql = "SELECT min(idShot) as idShot FROM (SELECT "+GMContract.ShotTable.ID_SHOT +" FROM "+ GMContract.ShotTable.TABLE+" order by "+ GMContract.ShotTable.ID_SHOT+" DESC LIMIT 10)";
        int idShot = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            idShot = c.getInt(c.getColumnIndex(GMContract.ShotTable.ID_SHOT));
        }
        c.close();
        return db.delete(GMContract.ShotTable.TABLE, GMContract.ShotTable.ID_SHOT+"<"+idShot,null);
    }

    public  Long getFirstModifiedDate(String entity){
        Long firstDateModified;
        if(isTableEmpty(entity)){
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

    public Long getLastModifiedDate(String entity) {
        Long lastDateModified;
        if(isTableEmpty(entity)){
            lastDateModified = 0L;
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

}
