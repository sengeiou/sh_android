package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.SynchroEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class SynchroEntityDBMapper {

  public static final String TIMESTAMP = DatabaseContract.SynchroTable.TIMESTAMP;
  public static final String ENTITY = DatabaseContract.SynchroTable.ENTITY;

  @Inject public SynchroEntityDBMapper() {
  }

  public SynchroEntity fromCursor(Cursor c) {
    SynchroEntity synchroEntity = new SynchroEntity();
    synchroEntity.setEntity(c.getString(c.getColumnIndex(ENTITY)));
    synchroEntity.setTimestamp(c.getLong(c.getColumnIndex(TIMESTAMP)));
    return synchroEntity;
  }

  public ContentValues toContentValues(SynchroEntity synchroEntity) {
    ContentValues cv = new ContentValues();
    cv.put(TIMESTAMP, synchroEntity.getTimestamp());
    cv.put(ENTITY, synchroEntity.getEntity());
    return cv;
  }
}
