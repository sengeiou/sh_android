package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.ShotTable;
import com.shootr.android.db.mappers.ShotEntityDBMapper;
import com.shootr.android.domain.StreamTimelineParameters;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotManager extends AbstractManager {

    @Inject ShotEntityDBMapper shotEntityMapper;

    private static final String SHOT_TABLE = ShotTable.TABLE;

    @Inject
    public ShotManager(SQLiteOpenHelper openHelper, ShotEntityDBMapper shotEntityMapper) {
        super(openHelper);
        this.shotEntityMapper = shotEntityMapper;
    }

    public void saveShot(ShotEntity shot) {
        insertShot(shot, getWritableDatabase());
    }

    public void saveShots(List<ShotEntity> shotList) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            for (ShotEntity shot : shotList) {
                insertShot(shot, database);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public List<ShotEntity> getShotsFromUser(String idUser, Integer limit) {
        String whereSelection = ShotTable.ID_USER + " = ?";
        String[] whereArguments = new String[] { idUser };

        return readShots(whereSelection, whereArguments, String.valueOf(limit));
    }

    public List<ShotEntity> getAllShotsFromUser(String idUser) {
        String whereSelection = ShotTable.ID_USER + " = ?";
        String[] whereArguments = new String[] { idUser };

        return readShots(whereSelection, whereArguments);
    }

    public List<ShotEntity> getShotsByStreamParameters(StreamTimelineParameters parameters) {
        String streamSelection = ShotTable.ID_STREAM + " = ?";
        String typeSelection = ShotTable.TYPE + " = ?";

        String[] whereArguments = new String[2];
        whereArguments[0] = String.valueOf(parameters.getStreamId());
        whereArguments[1] = String.valueOf(parameters.getShotType());
        String whereClause = streamSelection + " AND " + typeSelection;

        return readShots(whereClause, whereArguments);
    }

    public ShotEntity getShotById(String idShot) {
        String whereClause = ShotTable.ID_SHOT + " = ?";
        String[] whereArguments = new String[] { String.valueOf(idShot) };

        return readShot(whereClause, whereArguments);
    }

    public List<ShotEntity> getRepliesTo(String shotParentId) {
        String whereClause = ShotTable.ID_SHOT_PARENT + " = ?";
        String[] whereArguments = new String[] { shotParentId };

        return readShots(whereClause, whereArguments);
    }

    public List<ShotEntity> getStreamMediaShots(String idStream, List<String> idUsers) {
        String usersSelection = ShotTable.ID_USER + " IN (" + createListPlaceholders(idUsers.size()) + ")";
        String streamSelection = ShotTable.ID_STREAM + " = ?";
        String imageSelection = ShotTable.IMAGE + " IS NOT NULL ";

        String[] whereArguments = new String[idUsers.size() + 1];

        for (int i = 0; i < idUsers.size(); i++) {
            whereArguments[i] = String.valueOf(idUsers.get(i));
        }

        whereArguments[idUsers.size()] = String.valueOf(idStream);

        String whereClause = usersSelection + " AND " + streamSelection + " AND " + imageSelection;

        return readShots(whereClause, whereArguments);
    }

    public long deleteShot(String idShot) {
        String where = DatabaseContract.ShotTable.ID_SHOT + "=?";
        String[] whereArgs = new String[] { idShot };
        return (long) getWritableDatabase().delete(SHOT_TABLE, where, whereArgs);
    }

    @NonNull
    private List<ShotEntity> readShots(String whereClause, String[] whereArguments) {
        return readShots(whereClause, whereArguments, null);
    }

    @NonNull
    private List<ShotEntity> readShots(String whereClause, String[] whereArguments, String limit) {
        Cursor queryResult = getReadableDatabase().query(ShotTable.TABLE,
          ShotTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          ShotTable.BIRTH + " DESC",
          limit);

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

    @Nullable
    private ShotEntity readShot(String whereClause, String[] whereArguments) {
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

    private void insertShot(ShotEntity shot, SQLiteDatabase writableDatabase) {
        ContentValues contentValues = shotEntityMapper.toContentValues(shot);
        if (contentValues.getAsLong(DatabaseContract.SyncColumns.DELETED) != null) {
            deleteShot(shot.getIdShot());
        } else {
            writableDatabase.insertWithOnConflict(SHOT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }
}
