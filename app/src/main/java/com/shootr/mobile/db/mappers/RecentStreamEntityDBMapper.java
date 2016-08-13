package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.RecentStreamEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class RecentStreamEntityDBMapper extends GenericDBMapper {

  @Inject public RecentStreamEntityDBMapper() {
  }

  public ContentValues toContentValues(RecentStreamEntity recentStreamEntity) {
    ContentValues contentValues = new ContentValues();
    fillContentValues(recentStreamEntity, contentValues);
    return contentValues;
  }

  private void fillContentValues(RecentStreamEntity recentStreamEntity,
      ContentValues contentValues) {
    contentValues.put(DatabaseContract.RecentStreamTable.ID_STREAM,
        recentStreamEntity.getStream().getIdStream());
    contentValues.put(DatabaseContract.RecentStreamTable.JOIN_STREAM_DATE,
        recentStreamEntity.getJoinStreamDate());
  }

  public RecentStreamEntity fromCursor(Cursor c) {
    RecentStreamEntity recentStreamEntity = new RecentStreamEntity();
    fillStreamEntity(c, recentStreamEntity);
    return recentStreamEntity;
  }

  private void fillStreamEntity(Cursor c, RecentStreamEntity recentStreamEntity) {
    StreamEntity stream = new StreamEntity();
    stream.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.RecentStreamTable.ID_STREAM)));
    recentStreamEntity.setStream(stream);
    recentStreamEntity.setJoinStreamDate(
        c.getLong(c.getColumnIndex(DatabaseContract.RecentStreamTable.JOIN_STREAM_DATE)));
  }
}
