package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.SynchroEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.SynchroEntityDBMapper;
import javax.inject.Inject;

public class SynchroManager extends AbstractManager {

  private static final String SYNCHRO_TABLE = DatabaseContract.SynchroTable.TABLE;

  private final SynchroEntityDBMapper synchroEntityDBMapper;

  @Inject public SynchroManager(SQLiteOpenHelper dbHelper,
      SynchroEntityDBMapper synchroEntityDBMapper) {
    super(dbHelper);
    this.synchroEntityDBMapper = synchroEntityDBMapper;
  }

  public void putEntity(SynchroEntity synchroEntity) {
    if (synchroEntity != null) {
      ContentValues contentValues = synchroEntityDBMapper.toContentValues(synchroEntity);
      getWritableDatabase().insertWithOnConflict(SYNCHRO_TABLE, null, contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public Long getTimestamp(String entity) {
    Long timestamp = 0L;
    String whereSelection = DatabaseContract.SynchroTable.ENTITY + " = ?";
    String[] whereArguments = new String[] { entity };
    Cursor c =
        getReadableDatabase().query(SYNCHRO_TABLE, DatabaseContract.SynchroTable.PROJECTION,
            whereSelection, whereArguments, null, null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      timestamp = synchroEntityDBMapper.fromCursor(c).getTimestamp();
    }
    c.close();
    return timestamp;
  }
}
