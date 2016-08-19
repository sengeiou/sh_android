package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.PollEntityDBMapper;
import javax.inject.Inject;

public class PollManager extends AbstractManager {

  private static final String POLL_TABLE = DatabaseContract.PollTable.TABLE;

  private final PollEntityDBMapper pollEntityDBMapper;

  @Inject protected PollManager(SQLiteOpenHelper dbHelper, PollEntityDBMapper pollEntityDBMapper) {
    super(dbHelper);
    this.pollEntityDBMapper = pollEntityDBMapper;
  }

  public PollEntity getPoll(String idStream) {
    String whereSelection = DatabaseContract.PollTable.ID_STREAM + " = ?";
    String[] whereArguments = new String[] { idStream };

    Cursor queryResult = getReadableDatabase().query(POLL_TABLE,
        DatabaseContract.PollTable.PROJECTION,
        whereSelection,
        whereArguments,
        null,
        null,
        null);

    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      PollEntity pollEntity = pollEntityDBMapper.fromCursor(queryResult);
      queryResult.close();
      return pollEntity;
    } else {
      return null;
    }
  }

  public void putPoll(PollEntity pollEntity) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();
      ContentValues contentValues = pollEntityDBMapper.toContentValues(pollEntity);
      database.insertWithOnConflict(DatabaseContract.PollTable.TABLE,
          null,
          contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  public PollEntity getPollbyIdPoll(String idPoll) {
    String whereSelection = DatabaseContract.PollTable.ID_POLL + " = ?";
    String[] whereArguments = new String[] { idPoll };

    Cursor queryResult = getReadableDatabase().query(POLL_TABLE,
        DatabaseContract.PollTable.PROJECTION,
        whereSelection,
        whereArguments,
        null,
        null,
        null);

    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      PollEntity pollEntity = pollEntityDBMapper.fromCursor(queryResult);
      queryResult.close();
      return pollEntity;
    } else {
      return null;
    }
  }

  public void removePolls(String idStream) {
    String whereClause = DatabaseContract.PollTable.ID_STREAM + " = ?";
    String[] whereArgs = new String[] { idStream };
    getWritableDatabase().delete(DatabaseContract.PollTable.TABLE, whereClause, whereArgs);
  }
}
