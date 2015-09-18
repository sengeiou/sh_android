package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.domain.StreamTimelineParameters;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotManager extends  AbstractManager{

    @Inject ShotEntityMapper shotEntityMapper;

    private static final String SHOT_TABLE = ShotTable.TABLE;

    @Inject
    public ShotManager(SQLiteOpenHelper openHelper, ShotEntityMapper shotEntityMapper){
        super(openHelper);
        this.shotEntityMapper = shotEntityMapper;
    }

    /**
     * Insert a Shot
     */
    public void saveShot(ShotEntity shot) throws SQLException {
        ContentValues contentValues = shotEntityMapper.toContentValues(shot);
        if (contentValues.getAsLong(DatabaseContract.SyncColumns.DELETED) != null) {
            deleteShot(shot);
        } else {
            getWritableDatabase().insertWithOnConflict(SHOT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public List<ShotEntity> getShotsFromUser(String idUser, Integer latestShotsNumber) {
        List<ShotEntity> latestShots = new ArrayList<>();
        String whereSelection = ShotTable.ID_USER + " = ?";
        String[] whereArguments = new String[]{idUser};

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE,
            ShotTable.PROJECTION,
            whereSelection,
            whereArguments,
            null,
            null,
            ShotTable.BIRTH + " DESC",
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

    public List<ShotEntity> getAllShotsFromUser(String idUser) {
        List<ShotEntity> latestShots = new ArrayList<>();
        String whereSelection = ShotTable.ID_USER + " = ?";
        String[] whereArguments = new String[]{idUser};

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE,
            ShotTable.PROJECTION,
            whereSelection,
            whereArguments,
            null,
            null,
            ShotTable.BIRTH + " DESC", null);

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
            if (shot.getDeleted() != null) {
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
        return deleteShot(shot.getIdShot());
    }

    public void insertInSync(){
        insertInTableSync(SHOT_TABLE, 3, 1000, 0);
    }

    public List<ShotEntity> getShotsByStreamParameters(StreamTimelineParameters parameters) {
        String streamSelection = ShotTable.ID_STREAM + " = ?";
        String typeSelection = ShotTable.TYPE + " = ?";
        //TODO since & max
        //TODO limit

        String[] whereArguments = new String[2];
        whereArguments[0] = String.valueOf(parameters.getStreamId());
        whereArguments[1] = String.valueOf(parameters.getShotType());
        String whereClause = streamSelection + " AND " + typeSelection;

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, whereArguments, null, null,
            ShotTable.BIRTH +" DESC");

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

    public List<ShotEntity> getStreamMediaShots(String idStream, List<String> idUsers) {
        String usersSelection = ShotTable.ID_USER + " IN (" + createListPlaceholders(idUsers.size()) + ")";
        String streamSelection = ShotTable.ID_STREAM + " = ?";
        String imageSelection = ShotTable.IMAGE + " IS NOT NULL ";

        String[] whereArguments = new String[idUsers.size()+1];

        for (int i = 0; i < idUsers.size(); i++) {
            whereArguments[i] = String.valueOf(idUsers.get(i));
        }

        whereArguments[idUsers.size()] = String.valueOf(idStream);

        String whereClause = usersSelection + " AND " + streamSelection + " AND " + imageSelection;

        Cursor queryResult =
          getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause, whereArguments, null, null,
            ShotTable.BIRTH +" DESC");

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

    public Long deleteShot(String idShot) {
        long res = 0;
        String args = DatabaseContract.ShotTable.ID_SHOT + "=?";
        String[] stringArgs = new String[]{idShot};
        Cursor c = getReadableDatabase().query(SHOT_TABLE, DatabaseContract.ShotTable.PROJECTION, args, stringArgs, null, null, null);
        if (c.getCount() > 0) {
            res = getWritableDatabase().delete(SHOT_TABLE, args, new String[]{idShot});
        }
        c.close();
        return res;
    }
}
