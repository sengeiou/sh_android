package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.DatabaseContract.UserTable;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.ShotEntityModelMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotManager extends  AbstractManager{

    @Inject ShotEntityMapper shotEntityMapper;
    @Inject UserMapper userMapper;
    @Inject ShotEntityModelMapper shotVOMapper;

    private static final String SHOT_TABLE = ShotTable.TABLE;
    private static final String CSYS_DELETED = DatabaseContract.SyncColumns.CSYS_DELETED;
    private static final String CSYS_BIRTH = DatabaseContract.SyncColumns.CSYS_BIRTH;

    @Inject
    public ShotManager(SQLiteOpenHelper openHelper, ShotEntityMapper shotEntityMapper, UserMapper userMapper, ShotEntityModelMapper shotVOMapper){
        super(openHelper);
        this.shotEntityMapper = shotEntityMapper;
        this.userMapper = userMapper;
        this.shotVOMapper = shotVOMapper;
    }

    /**
     * Insert a Shot
     */
    public void saveShot(ShotEntity shot) throws SQLException {
        ContentValues contentValues = shotEntityMapper.toContentValues(shot);
        if (contentValues.getAsLong(CSYS_DELETED) != null) {
            deleteShot(shot);
        } else {
            getWritableDatabase().insertWithOnConflict(SHOT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public List<ShotEntity> getLatestShotsFromIdUser(String idUser, Long latestShotsNumber) {
        List<ShotEntity> latestShots = new ArrayList<>();
        String whereSelection = ShotTable.ID_USER + " = ?";
        String[] whereArguments = new String[]{String.valueOf(idUser)};

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE,
            ShotTable.PROJECTION,
            whereSelection,
            whereArguments,
            null,
            null,
            ShotTable.CSYS_BIRTH + " DESC",
            String.valueOf(latestShotsNumber));

        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                latestShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return latestShots;
    }

    /**
     * Insert a shot list
     */
    public void saveShots(List<ShotEntity> shotList) {
        SQLiteDatabase database = getWritableDatabase();
        for (ShotEntity shot : shotList) {
            if (shot.getCsysDeleted() != null) {
                deleteShot(shot);
            } else {
                ContentValues contentValues = shotEntityMapper.toContentValues(shot);
                database.insertWithOnConflict(ShotTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
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
        insertInTableSync(SHOT_TABLE, 3, 1000, 0);
    }

    public List<ShotEntity> getShotsByParameters(TimelineParameters parameters) {
        List<String> userIds = parameters.getAllUserIds();
        String usersSelection = ShotTable.ID_USER + " IN (" + createListPlaceholders(userIds.size()) + ")";
        String eventSelection = ShotTable.ID_EVENT + (parameters.getEventId() != null ? " = ?" : " IS NULL");
        //TODO since & max
        //TODO limit

        String[] whereArguments = new String[parameters.getEventId() != null? userIds.size()+1 : userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            whereArguments[i] = String.valueOf(userIds.get(i));
        }
        if (parameters.getEventId() != null) {
            whereArguments[userIds.size()] = String.valueOf(parameters.getEventId());
        }
        String whereClause = usersSelection + " AND " + eventSelection;

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, whereArguments, null, null,
            ShotTable.CSYS_BIRTH+" DESC");

        List<ShotEntity> resultShots = new ArrayList<>(queryResult.getCount());
        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                resultShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultShots;
    }

    public ShotEntity getShotById(String idShot) {
        String whereClause = ShotTable.ID_SHOT + " = ?";
        String[] whereArguments = new String[]{String.valueOf(idShot)};

        Cursor queryResult = getReadableDatabase().query(ShotTable.TABLE,
          ShotTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          null);

        ShotEntity shotEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            shotEntity = shotEntityMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return shotEntity;
    }

    public List<ShotEntity> getRepliesTo(String shotParentId) {
        String whereClause = ShotTable.ID_SHOT_PARENT + " = ?";
        String[] whereArguments = new String[]{shotParentId};

        Cursor queryResult = getReadableDatabase().query(ShotTable.TABLE,
          ShotTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          null);

        List<ShotEntity> resultShots = new ArrayList<>(queryResult.getCount());
        ShotEntity shotEntity;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                shotEntity = shotEntityMapper.fromCursor(queryResult);
                resultShots.add(shotEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultShots;
    }
}
