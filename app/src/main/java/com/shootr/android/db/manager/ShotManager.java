package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.DatabaseContract.UserTable;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotManager extends  AbstractManager{

    @Inject ShotMapper shotMapper;
    @Inject UserMapper userMapper;
    @Inject ShotModelMapper shotVOMapper;

    private static final String SHOT_TABLE = ShotTable.TABLE;
    private static final String CSYS_DELETED = DatabaseContract.SyncColumns.CSYS_DELETED;
    private static final String CSYS_BIRTH = DatabaseContract.SyncColumns.CSYS_BIRTH;

    @Inject
    public ShotManager(SQLiteOpenHelper openHelper, ShotMapper shotMapper, UserMapper userMapper, ShotModelMapper shotVOMapper){
        super(openHelper);
        this.shotMapper = shotMapper;
        this.userMapper = userMapper;
        this.shotVOMapper = shotVOMapper;
    }

    /**
     * Insert a Shot
     */
    public void saveShot(ShotEntity shot) throws SQLException {
        ContentValues contentValues = shotMapper.toContentValues(shot);
        if (contentValues.getAsLong(CSYS_DELETED) != null) {
            deleteShot(shot);
        } else {
            getWritableDatabase().insertWithOnConflict(SHOT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public List<ShotModel> retrieveOldOrNewTimeLineWithUsers(List<ShotEntity> shots, Long currentUserId) {
        String idShots = "(";
        String idUsers = "(";
        for (ShotEntity shot : shots) {
            idUsers = idUsers.concat(shot.getIdUser() + ",");
            idShots = idShots.concat(shot.getIdShot() + ",");
        }
        idShots = idShots.concat(")");
        idShots = idShots.replace(",)", ")");
        idUsers = idUsers.concat(")");
        idUsers = idUsers.replace(",)", ")");
        String query = "SELECT "
          + ShotTable.ID_SHOT
          +
          ",b."
          + ShotTable.ID_USER+","
          + ShotTable.COMMENT+","
          + ShotTable.IMAGE
          + ",b."
          + UserTable.NAME
          +",b."
          + UserTable.FAVORITE_TEAM_ID
          +",b."
          + UserTable.FAVORITE_TEAM_NAME
          +
          ",b."
          + UserTable.NUM_FOLLOWERS
          +
          ",b."
          + UserTable.NUM_FOLLOWINGS
          +
          ",b."
          + UserTable.BIO
          +
          ",b."
          + UserTable.POINTS
          +
          ",b."
          + UserTable.WEBSITE
          +
          ",b."
          + UserTable.RANK
          +
          ",b."
          + UserTable.PHOTO
          + ","
          + UserTable.USER_NAME
          + ",a."
          + ShotTable.CSYS_SYNCHRONIZED
          + ",a."
          + CSYS_BIRTH
          + ",a."
          + ShotTable.CSYS_REVISION
          + ",a."
          + ShotTable.CSYS_MODIFIED
          + ",a."
          + CSYS_DELETED
          +
          " FROM "
          + SHOT_TABLE
          + " a "
          + " INNER JOIN "
          + UserTable.TABLE
          + " b "
          +
          "ON a."
          + ShotTable.ID_USER
          + " = b."
          + UserTable.ID
          + " WHERE b."
          + ShotTable.ID_USER
          + " IN "
          + idUsers
          + " AND "
          + ShotTable.ID_SHOT
          + " IN "
          + idShots
          + " ORDER BY a."
          + CSYS_BIRTH
          + " DESC;";
        Timber.d("Executing query: %s", query);
        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        int count = cursor.getCount();
        if (count == 0) {
            return new ArrayList<>(0);
        }
        List<ShotModel> shotList = new ArrayList<>(count);
        cursor.moveToFirst();
        do {

            ShotEntity shot = shotMapper.fromCursor(cursor);
            ShotModelMapper shotModelMapper = new ShotModelMapper();
            UserEntity user = userMapper.fromCursor(cursor);
            ShotModel shotModel = shotModelMapper.toShotModel(user, shot);
            if (user != null) {
                shotList.add(shotModel);
            } else {
                Timber.e("No User found for Shot with id %d and userId %d", shot.getIdShot(), shot.getIdUser());
            }
        } while (cursor.moveToNext());

        cursor.close();
        return shotList;
    }


    public List<ShotEntity> getLatestShotsFromIdUser(Long idUser, Long latestShotsNumber) {
        List<ShotEntity> latestShots = new ArrayList<>();
        String whereSelection = ShotTable.ID_USER + " = ?";
        String[] whereArguments = new String[]{String.valueOf(idUser)};

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereSelection, whereArguments, null, null,
            ShotTable.CSYS_BIRTH+" DESC", String.valueOf(latestShotsNumber));

        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotMapper.fromCursor(queryResult);
                latestShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return latestShots;
    }

    public List<ShotModel> retrieveTimelineWithUsers(Long currentUserId) {
        String query = "SELECT " + ShotTable.ID_SHOT +
                ",b." + ShotTable.ID_USER + ","
                + ShotTable.COMMENT+ ","
                + ShotTable.IMAGE +
                ",b."+UserTable.FAVORITE_TEAM_ID+
                ",b."+UserTable.FAVORITE_TEAM_NAME+
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
        Cursor cursor = getReadableDatabase().rawQuery(query, null);


        int count = cursor.getCount();
        if (count == 0) {
            return new ArrayList<>(0);
        }
        List<ShotModel> shots = new CopyOnWriteArrayList();
        cursor.moveToFirst();
        do {

            ShotEntity shot = shotMapper.fromCursor(cursor);

            ShotModelMapper shotModelMapper = new ShotModelMapper();
            UserEntity user = userMapper.fromCursor(cursor);
            ShotModel shotModel = shotModelMapper.toShotModel(user,shot);
            if (user != null) {
                shots.add(shotModel);
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
    public void saveShots(List<ShotEntity> shotList) {
        long res;
        Collections.reverse(shotList);
        for (ShotEntity shot : shotList) {
            ContentValues contentValues = shotMapper.toContentValues(shot);
            if (contentValues.getAsLong(CSYS_DELETED) != null) {
                res = deleteShot(shot);
            } else {
                res = getReadableDatabase().insertWithOnConflict(ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Timber.d("Shot inserted with result: %d", res);
            }
           insertInSync();
        }
    }

    /**
     * Delete a shot
     */
    public long deleteShot(ShotEntity shot) {
        long res = 0;
        String args = DatabaseContract.ShotTable.ID_SHOT + "=?";
        String[] stringArgs = new String[]{String.valueOf(shot.getIdShot())};
        Cursor c = getReadableDatabase().query(SHOT_TABLE, DatabaseContract.ShotTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = getWritableDatabase().delete(SHOT_TABLE, DatabaseContract.ShotTable.ID_SHOT, new String[]{String.valueOf(shot.getIdShot())});
        }
        c.close();
        return res;
    }

    public void insertInSync(){
        insertInTableSync(SHOT_TABLE,3,1000,0);
    }

    public ShotEntity retrieveLastShotFromUser(Long userId) {
        Cursor c = getReadableDatabase().query(SHOT_TABLE, ShotTable.PROJECTION, ShotTable.ID_USER + "=?",
          new String[] { String.valueOf(userId) }, null, null, CSYS_BIRTH + " DESC", "1");
        if (c.getCount() > 0) {
            c.moveToFirst();
            ShotEntity lastShot = shotMapper.fromCursor(c);
            c.close();
            return lastShot;
        }
        return null;
    }


    public boolean removeOldShots(SQLiteDatabase db){
        boolean res = true;
        long number;
        int maxNumRows = getNumMaxOfRowsByEntity(SHOT_TABLE);
        long currentNumRows = numberOfRows(SHOT_TABLE);
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
