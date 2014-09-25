package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.GMContract.SyncColumns;
import gm.mobi.android.db.GMContract.ShotTable;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.db.objects.User;
import timber.log.Timber;

public class ShotManager {

    private static final String TIMELINE_LIMIT = "200";

    /**
     * Insert a Shot
     */
    public static void saveShot(SQLiteDatabase db, Shot shot) throws SQLException {
        long res;
        ContentValues contentValues = ShotMapper.toContentValues(shot);
        if (contentValues.getAsLong(ShotTable.CSYS_DELETED) != null) {
            res = deleteShot(db, shot);
        } else {
            res = db.insertWithOnConflict(ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public static List<Shot> retrieveTimelineWithUsers(SQLiteDatabase db) {
        String query = "SELECT * FROM " + ShotTable.TABLE + " a "
                + " INNER JOIN " + UserTable.TABLE + " b " +
                "ON a." + ShotTable.ID_USER + " = b." + UserTable.ID +
                " ORDER BY "+ShotTable.CSYS_BIRTH+" DESC;";
        Timber.d("Executing query: %s", query);
        Cursor cursor = db.rawQuery(query, null);

        int count = cursor.getCount();
        if (count == 0) {
            return new ArrayList<>(0);
        }
        List<Shot> shots = new ArrayList<>(count);
        cursor.moveToFirst();
        do {
            Shot shot = ShotMapper.fromCursor(cursor);
            User user = UserMapper.fromCursor(cursor);
            if (user != null) {
                shot.setUser(user);
                shots.add(shot);
            } else {
                Timber.e("No User found for Shot with id %d and userId %d", shot.getIdShot(), shot.getIdUser());
            }
        } while (cursor.moveToNext());

        cursor.close();
        return shots;
    }

    /**
     * Insert a shot list
     */
    public static void saveShots(SQLiteDatabase db, List<Shot> shotList) {
        long res;
        for (Shot shot : shotList) {
            ContentValues contentValues = ShotMapper.toContentValues(shot);
//            db.beginTransaction();
            if (contentValues.getAsLong(ShotTable.CSYS_DELETED) != null) {
                res = deleteShot(db, shot);
            }else{
                res = db.insertWithOnConflict(ShotTable.TABLE,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
                Timber.d("Shot inserted with result: %d", res);
            }
            insertShotInTableSync(db);
//            db.endTransaction();
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
