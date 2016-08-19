package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.DatabaseContract.HighlightedShotTable;
import com.shootr.mobile.db.mappers.HighlightedShotEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class HighlightedShotManager extends AbstractManager {

  @Inject HighlightedShotEntityDBMapper highlightedShotEntityMapper;
  @Inject ShotManager shotManager;

  private static final String HIGHLIGHTED_SHOT_TABLE = HighlightedShotTable.TABLE;

  @Inject public HighlightedShotManager(SQLiteOpenHelper openHelper,
      HighlightedShotEntityDBMapper highlightedShotEntityMapper, ShotManager shotManager) {
    super(openHelper);
    this.highlightedShotEntityMapper = highlightedShotEntityMapper;
    this.shotManager = shotManager;
  }

  public void saveHighLightedShot(HighlightedShotEntity highlightedShot) {
    insertHighlightedShot(highlightedShot, getWritableDatabase());
  }

  public HighlightedShotEntity getHighlightedShotByIdStream(String idStream) {
    String whereClause = DatabaseContract.HighlightedShotTable.ID_STREAM + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idStream) };

    List<HighlightedShotEntity> resultHighlights =
        readHighlightedShots(whereClause, whereArguments);
    if (!resultHighlights.isEmpty()) {
      return resultHighlights.get(0);
    } else {
      return null;
    }
  }

  private void insertHighlightedShot(HighlightedShotEntity highlightedShot,
      SQLiteDatabase writableDatabase) {
    ContentValues contentValues = highlightedShotEntityMapper.toContentValues(highlightedShot);
    if (contentValues.getAsLong(DatabaseContract.SyncColumns.DELETED) != null) {
      deleteHighlightedShot(highlightedShot.getIdHighlightedShot());
    } else {
      writableDatabase.insertWithOnConflict(HIGHLIGHTED_SHOT_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  @NonNull
  private List<HighlightedShotEntity> readShots(String whereClause, String[] whereArguments) {
    return readHighlightedShots(whereClause, whereArguments);
  }

  public long deleteHighlightedShot(String idHighlightedShot) {
    String where = HighlightedShotTable.ID_HIGHLIGHTED_SHOT + "=?";
    String[] whereArgs = new String[] { idHighlightedShot };
    return (long) getWritableDatabase().delete(HIGHLIGHTED_SHOT_TABLE, where, whereArgs);
  }

  @NonNull private List<HighlightedShotEntity> readHighlightedShots(String whereClause,
      String[] whereArguments) {
    Cursor queryResult = getReadableDatabase().query(DatabaseContract.HighlightedShotTable.TABLE,
        DatabaseContract.HighlightedShotTable.PROJECTION, whereClause, whereArguments, null, null,
        DatabaseContract.HighlightedShotTable.BIRTH + " DESC");

    List<HighlightedShotEntity> resultHighlights = new ArrayList<>(queryResult.getCount());
    HighlightedShotEntity HighlightedShotEntity;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        HighlightedShotEntity = highlightedShotEntityMapper.fromCursor(queryResult);
        resultHighlights.add(HighlightedShotEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();
    return resultHighlights;
  }

  public void hideHighlightedShot(String idHighlightedShot) {
    String whereClause = HighlightedShotTable.ID_HIGHLIGHTED_SHOT + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idHighlightedShot) };

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.HighlightedShotTable.TABLE,
        DatabaseContract.HighlightedShotTable.PROJECTION,
        whereClause,
        whereArguments,
        null,
        null,
        null);

    HighlightedShotEntity shotEntity = null;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      shotEntity = highlightedShotEntityMapper.fromCursor(queryResult);
    }
    queryResult.close();

    if (shotEntity != null) {
      shotEntity.setVisible(false);
      saveHighLightedShot(shotEntity);
    }
  }
}
