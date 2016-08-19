package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.PollOptionEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.PollOptionEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PollOptionManager extends AbstractManager {

  private final PollOptionEntityDBMapper pollOptionEntityDBMapper;

  @Inject protected PollOptionManager(SQLiteOpenHelper dbHelper,
      PollOptionEntityDBMapper pollOptionEntityDBMapper) {
    super(dbHelper);
    this.pollOptionEntityDBMapper = pollOptionEntityDBMapper;
  }

  public List<PollOptionEntity> getPollOptions(String idPoll) {
    String whereSelection = DatabaseContract.PollOptionTable.ID_POLL + " = ? ";
    String[] whereArguments = new String[] { idPoll };

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.PollOptionTable.TABLE,
        DatabaseContract.PollOptionTable.PROJECTION,
        whereSelection,
        whereArguments,
        null,
        null,
        null,
        null);

    List<PollOptionEntity> results = new ArrayList<>(queryResult.getCount());
    PollOptionEntity pollOptionEntity;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        pollOptionEntity = pollOptionEntityDBMapper.fromCursor(queryResult);
        results.add(pollOptionEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();
    return results;
  }

  public void putPollOptions(String idPoll, List<PollOptionEntity> pollOptions) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();
      for (PollOptionEntity pollOption : pollOptions) {
        pollOption.setIdPoll(idPoll);
        putPollOption(pollOption, database);
      }
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  private void putPollOption(PollOptionEntity pollOption, SQLiteDatabase database) {
    ContentValues contentValues = pollOptionEntityDBMapper.toContentValues(pollOption);
    database.insertWithOnConflict(DatabaseContract.PollOptionTable.TABLE,
        null,
        contentValues,
        SQLiteDatabase.CONFLICT_REPLACE);
  }
}
