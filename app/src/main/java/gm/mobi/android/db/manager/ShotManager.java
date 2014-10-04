package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.GMContract.ShotTable;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.TableSync;
import gm.mobi.android.db.objects.User;
import timber.log.Timber;

public class ShotManager extends  AbstractManager{


    @Inject
    public ShotManager(){

    }

    /**
     * Insert a Shot
     */
    public void saveShot(Shot shot) throws SQLException {

        ContentValues contentValues = ShotMapper.toContentValues(shot);
        if (contentValues.getAsLong(ShotTable.CSYS_DELETED) != null) {
            deleteShot(shot);
        } else {
            db.insertWithOnConflict(ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertShotInTableSync(shot.getCsys_modified());
    }

    public List<Shot> retrieveOldOrNewTimeLineWithUsers(List<Shot> shots) {
        String idShots = "(";
        String idUsers = "(";
        for (Shot shot : shots) {
            idUsers = idUsers.concat(shot.getIdUser() + ",");
            idShots = idShots.concat(shot.getIdShot() + ",");
        }
        idShots = idShots.concat(")");
        idShots = idShots.replace(",)", ")");
        idUsers = idUsers.concat(")");
        idUsers = idUsers.replace(",)", ")");
        String query = "SELECT " + ShotTable.ID_SHOT +
                ",b." + ShotTable.ID_USER + "," + ShotTable.COMMENT + ",b." + UserTable.NAME +
                ", b."+UserTable.FAVOURITE_TEAM_ID+
                ",b."+UserTable.NUM_FOLLOWERS+
                ",b."+UserTable.NUM_FOLLOWINGS+
                ",b."+UserTable.BIO+
                ",b."+UserTable.POINTS+
                ",b."+UserTable.WEBSITE+
                ",b."+UserTable.RANK+
                ",b." + UserTable.PHOTO + "," + UserTable.USER_NAME + ",a." + ShotTable.CSYS_SYNCHRONIZED + ",a." + ShotTable.CSYS_BIRTH + ",a." + ShotTable.CSYS_REVISION + ",a." + ShotTable.CSYS_MODIFIED + ",a." + ShotTable.CSYS_DELETED +
                " FROM " + ShotTable.TABLE + " a "
                + " INNER JOIN " + UserTable.TABLE + " b " +
                "ON a." + ShotTable.ID_USER + " = b." + UserTable.ID
                + " WHERE b." + ShotTable.ID_USER + " IN " + idUsers + " AND " + ShotTable.ID_SHOT + " IN " + idShots + " ORDER BY a." + ShotTable.CSYS_BIRTH + " DESC;";
        Timber.d("Executing query: %s", query);
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        if (count == 0) {
            return new ArrayList<>(0);
        }
        List<Shot> shotList = new ArrayList<>(count);
        cursor.moveToFirst();
        do {
            Shot shot = ShotMapper.fromCursor(cursor);
            User user = UserMapper.fromCursor(cursor);
            if (user != null) {
                shot.setUser(user);
                shotList.add(shot);
            } else {
                Timber.e("No User found for Shot with id %d and userId %d", shot.getIdShot(), shot.getIdUser());
            }
        } while (cursor.moveToNext());

        cursor.close();
        return shotList;
    }

    public List<Shot> retrieveTimelineWithUsers() {
        String query = "SELECT " + ShotTable.ID_SHOT +
                ",b." + ShotTable.ID_USER + ","
                + ShotTable.COMMENT +
                ",b."+UserTable.FAVOURITE_TEAM_ID+
                ",b." +UserTable.BIO +
                ",b." + UserTable.WEBSITE +
                ",b." + UserTable.RANK +
                ",b." + UserTable.NAME +
                ","+ UserTable.POINTS+
                ","+ UserTable.NUM_FOLLOWERS+
                ","+ UserTable.NUM_FOLLOWINGS+
                ","+ UserTable.PHOTO +
                "," + UserTable.USER_NAME +
                ",a." + ShotTable.CSYS_SYNCHRONIZED + ",a." + ShotTable.CSYS_BIRTH + ",a." + ShotTable.CSYS_REVISION + ",a." + ShotTable.CSYS_MODIFIED + ",a." + ShotTable.CSYS_DELETED +
                " FROM " + ShotTable.TABLE + " a "
                + " INNER JOIN " + UserTable.TABLE + " b " +
                "ON a." + ShotTable.ID_USER + " = b." + UserTable.ID +
                " ORDER BY a." + ShotTable.CSYS_BIRTH + " DESC;";
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
    public void saveShots(List<Shot> shotList) {
        long res;
        Collections.reverse(shotList);
        for (Shot shot : shotList) {
            ContentValues contentValues = ShotMapper.toContentValues(shot);
            if (contentValues.getAsLong(ShotTable.CSYS_DELETED) != null) {
                res = deleteShot(shot);
            } else {
                res = db.insertWithOnConflict(ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Timber.d("Shot inserted with result: %d", res);
            }
            insertShotInTableSync(shot.getCsys_modified());
        }
    }

    /**
     * Delete a shot
     */
    public long deleteShot(Shot shot) {
        long res = 0;
        String args = GMContract.ShotTable.ID_SHOT + "=?";
        String[] stringArgs = new String[]{String.valueOf(shot.getIdShot())};
        Cursor c = db.query(GMContract.ShotTable.TABLE, GMContract.ShotTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(GMContract.ShotTable.TABLE, GMContract.ShotTable.ID_SHOT, new String[]{String.valueOf(shot.getIdShot())});
        }
        c.close();
        return res;
    }

    public long insertShotInTableSync(Date dateModified) {
        TableSync tablesSync = new TableSync();
        tablesSync.setOrder(3); // It's the third data type the application insert in database
        tablesSync.setDirection("BOTH");
        tablesSync.setEntity(GMContract.ShotTable.TABLE);
        tablesSync.setMax_timestamp(dateModified.getTime());
        if (isTableEmpty(GMContract.ShotTable.TABLE)) {
            tablesSync.setMin_timestamp(dateModified.getTime());
        }
        tablesSync.setMaxRows(1000);
        tablesSync.setMinRows(0);
        return insertOrUpdateSyncTable(tablesSync);
    }

    public static Shot retrieveLastShotFromUser(SQLiteDatabase db, Long userId) {
        Cursor c = db.query(ShotTable.TABLE, ShotTable.PROJECTION, ShotTable.ID_USER + "=?", new String[]{String.valueOf(userId)}, null, null, ShotTable.CSYS_BIRTH + " DESC", "1");
        if (c.getCount() > 0) {
            c.moveToFirst();
            Shot lastShot = ShotMapper.fromCursor(c);
            c.close();
            return lastShot;
        }
        return null;
    }


    public boolean removeOldShots(SQLiteDatabase db){
        boolean res = true;
        long number;
        int maxNumRows = getNumMaxOfRowsByEntity(GMContract.ShotTable.TABLE);
        long currentNumRows = numberOfRows( GMContract.ShotTable.TABLE);
        if(maxNumRows<currentNumRows){
            //We have to delete the older ones
            number = currentNumRows-maxNumRows;
            int resRemoved = deleteRows(number);
            Timber.e("Borrar los más antiguos. Borrados: %d",resRemoved);
        }else{
            Timber.e("El numero de filas en la tabla Shot es menor que el número máximo");
        }
        return res;
    }
}
