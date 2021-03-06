package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.DatabaseContract.ShotTable;
import com.shootr.mobile.db.mappers.ShotEntityDBMapper;
import com.shootr.mobile.domain.model.stream.StreamTimelineParameters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class ShotManager extends AbstractManager {

  private static final String DEFAULT_SHOTS_LIMIT = "100";
  @Inject ShotEntityDBMapper shotEntityMapper;

  private static final String SHOT_TABLE = ShotTable.TABLE;

  @Inject public ShotManager(SQLiteOpenHelper openHelper, ShotEntityDBMapper shotEntityMapper) {
    super(openHelper);
    this.shotEntityMapper = shotEntityMapper;
  }

  public void saveShot(ShotEntity shot, String idUserMe) {
    setPaddingtoShot(shot, idUserMe);
    insertShot(shot, getWritableDatabase());
  }

  private void setPaddingtoShot(ShotEntity shot, String idUserMe) {
    if (shot.getIdUser().equals(idUserMe)) {
      shot.setPadding(0);
    } else if (shot.isPadding() == null) {
      shot.setPadding(readPadding(shot.getIdShot()));
    }
  }

  public void saveShots(List<ShotEntity> shotList, String idUserMe) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();
      for (ShotEntity shot : shotList) {
        setPaddingtoShot(shot, idUserMe);
        insertShot(shot, database);
      }
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  public boolean hasNewFilteredShots(String idStream, String lastTimeFiltered, String idUser) {
    String whereSelection = ShotTable.ID_STREAM
        + " = ? and "
        + ShotTable.BIRTH
        + " > ? and "
        + ShotTable.IS_PADDING
        + " <> 1 and "
        + ShotTable.ID_USER
        + " <> ?";
    String[] whereArguments = new String[] { idStream, lastTimeFiltered, idUser };

    return readShots(whereSelection, whereArguments, null).size() > 0;
  }

  private Integer readPadding(String idShot) {
    String whereClause = ShotTable.ID_SHOT + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idShot) };
    String[] selection = new String[] { ShotTable.IS_PADDING };
    Cursor queryResult =
        getReadableDatabase().query(ShotTable.TABLE, selection, whereClause, whereArguments, null,
            null, null);

    Integer padding = null;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      padding = queryResult.getInt(0);
    }
    queryResult.close();
    return padding;
  }

  public List<ShotEntity> getProfileShots(String idUser, Integer limit) {
    String whereSelection = ShotTable.RESHOOTED + " = 1";

    return readProfileShots(whereSelection, null, String.valueOf(limit));
  }

  private List<ShotEntity> readProfileShots(String whereClause, String[] whereArguments, String limit) {
    Cursor queryResult =
        getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause,
            whereArguments, null, null, ShotTable.RESHOOTED_TIME + " DESC", limit);

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

  public List<ShotEntity> getShotsFromUser(String idUser, Integer limit) {
    String whereSelection = ShotTable.ID_USER + " = ? and " + ShotTable.PROFILE_HIDDEN + " IS NULL";
    String[] whereArguments = new String[] { idUser };

    return readShots(whereSelection, whereArguments, String.valueOf(limit));
  }

  public List<ShotEntity> getAllShotsFromUser(String idUser) {
    String whereSelection = ShotTable.ID_USER + " = ? and " + ShotTable.PROFILE_HIDDEN + " IS NULL";
    String[] whereArguments = new String[] { idUser };

    return readShots(whereSelection, whereArguments);
  }

  public List<ShotEntity> getShotsByStreamParameters(StreamTimelineParameters parameters) {
    String streamSelection = ShotTable.ID_STREAM + " = ?";

    String[] whereArguments = new String[1];
    whereArguments[0] = String.valueOf(parameters.getStreamId());
    String whereClause = streamSelection;
    String limit =
        (parameters.getLimit() != null) ? parameters.getLimit().toString() : DEFAULT_SHOTS_LIMIT;

    return readShots(whereClause, whereArguments, limit);
  }

  public List<ShotEntity> getShotsByStreamParametersFiltered(StreamTimelineParameters parameters) {
    String streamSelection = ShotTable.ID_STREAM + " = ? AND " + ShotTable.IS_PADDING + " = 0";

    String[] whereArguments = new String[1];
    whereArguments[0] = String.valueOf(parameters.getStreamId());
    String whereClause = streamSelection;

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

  public List<ShotEntity> getStreamMediaShots(String idStream) {

    String streamSelection = ShotTable.ID_STREAM + " = ?";
    String imageSelection = ShotTable.IMAGE + " IS NOT NULL ";
    String[] whereArguments = new String[] { idStream };

    String whereClause = streamSelection + " AND " + imageSelection;

    return readShots(whereClause, whereArguments);
  }

  public long deleteShot(String idShot) {
    String where = DatabaseContract.ShotTable.ID_SHOT + "=?";
    String[] whereArgs = new String[] { idShot };
    return (long) getWritableDatabase().delete(SHOT_TABLE, where, whereArgs);
  }

  @NonNull private List<ShotEntity> readShots(String whereClause, String[] whereArguments) {
    return readShots(whereClause, whereArguments, null);
  }

  @NonNull
  private List<ShotEntity> readShots(String whereClause, String[] whereArguments, String limit) {
    Cursor queryResult =
        getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause,
            whereArguments, null, null, ShotTable.BIRTH + " DESC", limit);

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

  @Nullable private ShotEntity readShot(String whereClause, String[] whereArguments) {
    Cursor queryResult =
        getReadableDatabase().query(ShotTable.TABLE, ShotTable.PROJECTION, whereClause,
            whereArguments, null, null, null);

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
      writableDatabase.insertWithOnConflict(SHOT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public List<ShotEntity> getUserShotsByParameters(StreamTimelineParameters timelineParameters) {
    String streamSelection = ShotTable.ID_STREAM + " = ?";
    String typeSelection = ShotTable.TYPE + " = ?";
    String userSelection = ShotTable.ID_USER + " = ?";

    String[] whereArguments = new String[3];
    whereArguments[0] = String.valueOf(timelineParameters.getStreamId());
    whereArguments[1] = String.valueOf(timelineParameters.getShotTypes()[0]);
    whereArguments[2] = String.valueOf(timelineParameters.getUserId());
    String whereClause = streamSelection + " AND " + typeSelection + " AND " + userSelection;

    return readShots(whereClause, whereArguments);
  }

  public void deleteShotsByIdStream(String idStream) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();

      String where = ShotTable.ID_STREAM + "=?";
      String[] whereArgs = new String[] { idStream };
      getWritableDatabase().delete(SHOT_TABLE, where, whereArgs);

      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  public void hideShot(String idShot, Long timestamp) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      ContentValues contentValues = new ContentValues(1);
      contentValues.put(ShotTable.PROFILE_HIDDEN, timestamp);
      String where = ShotTable.ID_SHOT + "=?";
      String[] whereArgs = new String[] { idShot };
      database.update(SHOT_TABLE, contentValues, where, whereArgs);
    } finally {
      database.close();
    }
  }

  public List<ShotEntity> getHiddenShotNotSynchronized() {
    List<ShotEntity> shotsToUpdate = new ArrayList<>();
    String args = DatabaseContract.SyncColumns.SYNCHRONIZED
        + "='N' OR "
        + DatabaseContract.SyncColumns.SYNCHRONIZED
        + "= 'D' OR "
        + DatabaseContract.SyncColumns.SYNCHRONIZED
        + "='U'";
    Cursor c =
        getReadableDatabase().query(SHOT_TABLE, DatabaseContract.ShotTable.PROJECTION, args, null,
            null, null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      do {
        shotsToUpdate.add(shotEntityMapper.fromCursor(c));
      } while (c.moveToNext());
    }
    c.close();
    return shotsToUpdate;
  }

  public void pinShot(String idShot) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      ContentValues contentValues = new ContentValues(1);
      contentValues.put(ShotTable.PROFILE_HIDDEN, "NULL");
      String where = ShotTable.ID_SHOT + "=?";
      String[] whereArgs = new String[] { idShot };
      database.update(SHOT_TABLE, contentValues, where, whereArgs);
    } finally {
      database.close();
    }
  }

  public List<ShotEntity> getShotsByIdShotsList(List<String> idShots) {
    List<ShotEntity> shots = new ArrayList<>();
    if (idShots.size() < 1) {
      return shots;
    }
    String whereClause = ShotTable.ID_SHOT + " IN (" + createListPlaceholders(idShots.size()) + ")";

    int whereArgumentsSize = idShots.size();
    String[] whereArguments = new String[whereArgumentsSize];

    for (int i = 0; i < idShots.size(); i++) {
      whereArguments[i] = idShots.get(i);
    }

    Cursor c =
        getReadableDatabase().query(SHOT_TABLE, DatabaseContract.ShotTable.PROJECTION, whereClause,
            whereArguments, null, null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      do {
        shots.add(shotEntityMapper.fromCursor(c));
      } while (c.moveToNext());
    }
    c.close();
    return shots;
  }

  public void reshoot(String idShot) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      ContentValues contentValues = new ContentValues(2);
      contentValues.put(ShotTable.RESHOOTED, 1);
      contentValues.put(ShotTable.RESHOOTED_TIME, new Date().getTime());
      String where = ShotTable.ID_SHOT + "= ?";
      String[] whereArgs = new String[] { idShot };
      database.update(SHOT_TABLE, contentValues, where, whereArgs);
    } finally {
      database.close();
    }
  }

  public void undoReshoot(String idShot) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      ContentValues contentValues = new ContentValues(2);
      contentValues.put(ShotTable.RESHOOTED, 0);
      contentValues.put(ShotTable.RESHOOTED_TIME, new Date().getTime());
      String where = ShotTable.ID_SHOT + "= ?";
      String[] whereArgs = new String[] { idShot };
      database.update(SHOT_TABLE, contentValues, where, whereArgs);
    } finally {
      database.close();
    }
  }

}
