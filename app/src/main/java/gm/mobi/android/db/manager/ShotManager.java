package gm.mobi.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.ShotTable;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.model.ShotVO;
import gm.mobi.android.ui.model.mappers.ShotVOMapper;
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

    private static final String SHOT_TABLE = ShotTable.TABLE;
    private static final String CSYS_DELETED = GMContract.SyncColumns.CSYS_DELETED;
    private static final String CSYS_BIRTH = GMContract.SyncColumns.CSYS_BIRTH;

    @Inject
    public ShotManager(ShotMapper shotMapper, UserMapper userMapper){
        this.shotMapper = shotMapper;
        this.userMapper = userMapper;
    }

    /**
     * Insert a Shot
     */
    public void saveShot(Shot shot) throws SQLException {
        ContentValues contentValues = shotMapper.toContentValues(shot);
        if (contentValues.getAsLong(CSYS_DELETED) != null) {
            deleteShot(shot);
        } else {
            db.insertWithOnConflict(SHOT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public List<ShotVO> retrieveOldOrNewTimeLineWithUsers(List<Shot> shots, Long currentUserId) {
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
        String query = "SELECT "
          + ShotTable.ID_SHOT
          +
          ",b."
          + ShotTable.ID_USER+","
          + ShotTable.COMMENT
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
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        if (count == 0) {
            return new ArrayList<>(0);
        }
        List<ShotVO> shotList = new ArrayList<>(count);
        cursor.moveToFirst();
        do {
            ShotVO shotVO = new ShotVO();
            Shot shot = shotMapper.fromCursor(cursor);
            ShotVOMapper shotVOMapper = new ShotVOMapper();
            FollowMapper followMapper = new FollowMapper();
            FollowManager followManager = new FollowManager(followMapper);
            followManager.setDataBase(db);
            User user = userMapper.fromCursor(cursor);
            Follow follow  = followManager.getFollowByUserIds(currentUserId,user.getIdUser());
            shotVO = shotVOMapper.toVO(user,follow,shot,currentUserId);
            if (user != null) {
                shotList.add(shotVO);
            } else {
                Timber.e("No User found for Shot with id %d and userId %d", shot.getIdShot(), shot.getIdUser());
            }
        } while (cursor.moveToNext());

        cursor.close();
        return shotList;
    }

    public List<ShotVO> retrieveTimelineWithUsers(Long currentUserId) {
        String query = "SELECT " + ShotTable.ID_SHOT +
                ",b." + ShotTable.ID_USER + ","
                + ShotTable.COMMENT +
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
        Cursor cursor = db.rawQuery(query, null);


        int count = cursor.getCount();
        if (count == 0) {
            return new ArrayList<>(0);
        }
        List<ShotVO> shots = new CopyOnWriteArrayList();
        cursor.moveToFirst();
        do {
            ShotVO shotVO = new ShotVO();
            Shot shot = shotMapper.fromCursor(cursor);

            ShotVOMapper shotVOMapper = new ShotVOMapper();
            FollowMapper followMapper = new FollowMapper();

            FollowManager followManager = new FollowManager(followMapper);
            followManager.setDataBase(db);

            User user = userMapper.fromCursor(cursor);
            Follow follow  = followManager.getFollowByUserIds(currentUserId,user.getIdUser());
            shotVO = shotVOMapper.toVO(user,follow,shot,currentUserId);
            if (user != null) {
                shots.add(shotVO);
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
            ContentValues contentValues = shotMapper.toContentValues(shot);
            if (contentValues.getAsLong(CSYS_DELETED) != null) {
                res = deleteShot(shot);
            } else {
                res = db.insertWithOnConflict(ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Timber.d("Shot inserted with result: %d", res);
            }
           insertInSync();
        }
    }

    /**
     * Delete a shot
     */
    public long deleteShot(Shot shot) {
        long res = 0;
        String args = GMContract.ShotTable.ID_SHOT + "=?";
        String[] stringArgs = new String[]{String.valueOf(shot.getIdShot())};
        Cursor c = db.query(SHOT_TABLE, GMContract.ShotTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = db.delete(SHOT_TABLE, GMContract.ShotTable.ID_SHOT, new String[]{String.valueOf(shot.getIdShot())});
        }
        c.close();
        return res;
    }

    public void insertInSync(){
        insertInTableSync(SHOT_TABLE,3,1000,0);
    }

    public Shot retrieveLastShotFromUser(SQLiteDatabase db, Long userId) {
        Cursor c = db.query(SHOT_TABLE, ShotTable.PROJECTION, ShotTable.ID_USER + "=?", new String[]{String.valueOf(userId)}, null, null, CSYS_BIRTH + " DESC", "1");
        if (c.getCount() > 0) {
            c.moveToFirst();
            Shot lastShot = shotMapper.fromCursor(c);
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
