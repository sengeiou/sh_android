package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.objects.Shot;

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
            }
            db.endTransaction();
        }
   }

    /***
     * Delete a shot
     * */
   public static long deleteShot(SQLiteDatabase db, Shot shot){
        return db.delete(GMContract.ShotTable.TABLE, GMContract.ShotTable.ID_SHOT,new String[]{String.valueOf(shot.getIdShot())});
   }
}
