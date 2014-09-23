package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.TableSync;
import timber.log.Timber;

public class ShotManager {

    /**
     * Insert a Shot
     * */
    public static void saveShot(SQLiteDatabase db, Shot shot) throws SQLException {
        long res;
        ContentValues contentValues = ShotMapper.toContentValues(shot);
        if(contentValues.getAsLong(GMContract.ShotTable.CSYS_DELETED)!=null){
            res = deleteShot(db,shot);
        }else{
            res = db.insertWithOnConflict(GMContract.ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertShotInTableSync(db);
    }

    /**
     * Insert a shot list
     * */
    public static void saveShots(SQLiteDatabase db, List<Shot> shotList){
        long res;
        for(Shot shot: shotList){
            ContentValues contentValues = ShotMapper.toContentValues(shot);
            db.beginTransaction();
            if(contentValues.getAsLong(GMContract.ShotTable.CSYS_DELETED) != null ){
                res = deleteShot(db, shot);
            }else{
                res = db.insertWithOnConflict(GMContract.ShotTable.TABLE,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
                Timber.d("Shot inserted", shot.getIdShot());
            }
            insertShotInTableSync(db);
            db.endTransaction();
        }
   }

    /***
     * Delete a shot
     * */
   public static long deleteShot(SQLiteDatabase db, Shot shot){
       long res = 0;
       String args = GMContract.ShotTable.ID_SHOT+"=?";
       String[] stringArgs = new String[]{String.valueOf(shot.getIdShot())};
       Cursor c = db.query(GMContract.ShotTable.TABLE, GMContract.ShotTable.PROJECTION, args, stringArgs, null, null, null);
       if (c.getCount() > 0) {
           res = db.delete(GMContract.ShotTable.TABLE, GMContract.ShotTable.ID_SHOT, new String[]{String.valueOf(shot.getIdShot())});
       }
       c.close();
       return res;
   }

    public static long insertShotInTableSync(SQLiteDatabase db){
        TableSync tablesSync = new TableSync();
        tablesSync.setOrder(3); // It's the third data type the application insert in database
        tablesSync.setDirection("BOTH");
        tablesSync.setEntity(GMContract.ShotTable.TABLE);
        tablesSync.setMax_timestamp(System.currentTimeMillis());
        if(GeneralManager.isTableEmpty(db, GMContract.ShotTable.TABLE)){
            tablesSync.setMin_timestamp(System.currentTimeMillis());
        }
        //We don't have this information already
//        tablesSync.setMaxRows();
//        tablesSync.setMinRows();
        return SyncTableManager.insertOrUpdateSyncTable(db,tablesSync);
    }



}
