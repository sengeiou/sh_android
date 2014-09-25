package gm.mobi.android.db.manager;

import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import gm.mobi.android.db.GMContract.TablesSync;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.util.TimeUtils;


public class SyncTableManager {

    public static int NUMDAYS = 15;
    
    public static Long getLastModifiedDate(SQLiteDatabase db, String entity) {
        Long lastDateModified = 0L;
        if(GeneralManager.isTableEmpty(db,entity)){
            lastDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
        }else{
            String sql = "SELECT * FROM "+TablesSync.TABLE +" WHERE "+TablesSync.ENTITY+" = '"+entity+"'";
            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                lastDateModified = c.getLong(c.getColumnIndex(TablesSync.MAX_TIMESTAMP));
            } else {
                lastDateModified = TimeUtils.getNDaysAgo(NUMDAYS);
            }
            c.close();
        }


        return lastDateModified;
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


//        int numMax = SyncTableManager.getNumMaxOfRowsByEntity(mDbHelper.getReadableDatabase(), GMContract.ShotTable.TABLE);
//        Timber.e("Número máximo de rows en la tabla %s es %d", GMContract.ShotTable.TABLE, numMax);

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
