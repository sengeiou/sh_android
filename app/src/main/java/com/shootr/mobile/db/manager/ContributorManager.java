package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.ContributorEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.ContributorDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ContributorManager extends AbstractManager {

  private final ContributorDBMapper mapper;

  @Inject public ContributorManager(SQLiteOpenHelper dbHelper, ContributorDBMapper mapper) {
    super(dbHelper);
    this.mapper = mapper;
  }

  public List<ContributorEntity> getContributorsByStream(String idStream) {
    String whereSelection = DatabaseContract.ContributorTable.ID_STREAM + " = ? ";
    String[] whereArguments = new String[] { idStream };

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.ContributorTable.TABLE,
        DatabaseContract.ContributorTable.PROJECTION,
        whereSelection,
        whereArguments,
        null,
        null,
        null);

    List<ContributorEntity> results = new ArrayList<>(queryResult.getCount());
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        results.add(mapper.fromCursor(queryResult));
      } while (queryResult.moveToNext());
    }
    queryResult.close();
    return results;
  }

  public void putContributors(List<ContributorEntity> contributors) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();
      for (ContributorEntity contributor : contributors) {
        ContentValues contentValues = mapper.toContentValues(contributor);
        database.insertWithOnConflict(DatabaseContract.ContributorTable.TABLE, null, contentValues,
            SQLiteDatabase.CONFLICT_REPLACE);
      }
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  public void clearContributors(String idStream) {
    String whereClause = DatabaseContract.ContributorTable.ID_STREAM + " = ?";
    String[] whereArgs = new String[] { idStream };
    getWritableDatabase().delete(DatabaseContract.ContributorTable.TABLE, whereClause, whereArgs);
  }
}
