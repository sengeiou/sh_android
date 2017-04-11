package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.RecentStreamEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.mappers.RecentStreamEntityDBMapper;
import com.shootr.mobile.db.mappers.StreamEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RecentStreamManager extends AbstractManager {

  @Inject StreamEntityDBMapper streamEntityMapper;
  @Inject RecentStreamEntityDBMapper recentStreamEntityDBMapper;
  @Inject StreamManager streamManager;

  @Inject
  public RecentStreamManager(SQLiteOpenHelper openHelper, StreamEntityDBMapper streamEntityDBMapper,
      RecentStreamEntityDBMapper recentStreamEntityDBMapper, StreamManager streamManager) {
    super(openHelper);
    this.streamEntityMapper = streamEntityDBMapper;
    this.recentStreamEntityDBMapper = recentStreamEntityDBMapper;
    this.streamManager = streamManager;
  }

  public List<RecentStreamEntity> readRecentStreams() {
    Cursor queryResult = getReadableDatabase().query(DatabaseContract.RecentSearchTable.TABLE,
        DatabaseContract.RecentSearchTable.PROJECTION,
        null,
        null,
        null,
        null,
        DatabaseContract.RecentSearchTable.JOIN_STREAM_DATE + " DESC");

    List<RecentStreamEntity> recentStreams = new ArrayList<>(queryResult.getCount());
    RecentStreamEntity recentStreamEntity;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        recentStreamEntity = recentStreamEntityDBMapper.fromCursor(queryResult);
        recentStreams.add(recentStreamEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();
    //TODO esto es una puta mierda y hay que refactorizar
    List<String> streamsIds = new ArrayList<>(recentStreams.size());
    for (RecentStreamEntity recentStream : recentStreams) {
      streamsIds.add(recentStream.getStream().getIdStream());
    }
    List<StreamEntity> streamsList = streamManager.getStreamsByIds(streamsIds);
    for (int position = 0; position < streamsIds.size(); position++) {
      for (StreamEntity streamEntity : streamsList) {
        if (streamEntity.getIdStream()
            .equals(recentStreams.get(position).getStream().getIdStream())) {
          recentStreams.get(position).setStream(streamEntity);
          break;
        }
      }
    }
    return recentStreams;
  }

  public void saveStream(RecentStreamEntity recentStreamEntity) {
    ContentValues contentValues = recentStreamEntityDBMapper.toContentValues(recentStreamEntity);
    getWritableDatabase().insertWithOnConflict(DatabaseContract.RecentSearchTable.TABLE, null,
        contentValues, SQLiteDatabase.CONFLICT_REPLACE);
  }
}
