package com.shootr.mobile.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.StreamSearchEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.db.DatabaseContract.TimelineSyncTable;
import com.shootr.mobile.db.mappers.FollowEntityDBMapper;
import com.shootr.mobile.db.mappers.StreamEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class StreamManager extends AbstractManager {

  private final StreamEntityDBMapper streamEntityMapper;
  private final FollowEntityDBMapper followMapper;
  private static final String FOLLOW_TABLE = DatabaseContract.FollowTable.TABLE;
  private static final String TYPE = DatabaseContract.FollowTable.TYPE;

  @Inject
  public StreamManager(SQLiteOpenHelper openHelper, StreamEntityDBMapper streamEntityMapper,
      FollowEntityDBMapper followMapper) {
    super(openHelper);
    this.streamEntityMapper = streamEntityMapper;
    this.followMapper = followMapper;
  }

  public StreamEntity getStreamById(String streamId) {
    String whereSelection = DatabaseContract.StreamTable.ID_STREAM + " = ?";
    String[] whereArguments = new String[] { String.valueOf(streamId) };

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.StreamTable.TABLE,
        DatabaseContract.StreamTable.PROJECTION, whereSelection, whereArguments, null, null, null);

    StreamEntity streamEntity = null;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      streamEntity = streamEntityMapper.fromCursor(queryResult);
    }
    queryResult.close();
    return streamEntity;
  }

  public List<StreamEntity> getStreamsByIds(List<String> streamIds) {
    if (streamIds.isEmpty()) {
      return new ArrayList<>();
    }
    String whereSelection = DatabaseContract.StreamTable.ID_STREAM
        + " IN ("
        + createListPlaceholders(streamIds.size())
        + ")";
    String[] whereArguments = new String[streamIds.size()];
    for (int i = 0; i < streamIds.size(); i++) {
      whereArguments[i] = streamIds.get(i);
    }

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.StreamTable.TABLE,
        DatabaseContract.StreamTable.PROJECTION, whereSelection, whereArguments, null, null, null);

    List<StreamEntity> resultEvents = new ArrayList<>(queryResult.getCount());
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        StreamEntity streamEntity = streamEntityMapper.fromCursor(queryResult);
        resultEvents.add(streamEntity);
      } while (queryResult.moveToNext());
    }
    queryResult.close();
    return resultEvents;
  }

  public void saveStreams(List<StreamEntity> eventEntities) {
    SQLiteDatabase database = getWritableDatabase();
    try {
      database.beginTransaction();
      for (StreamEntity streamEntity : eventEntities) {
        if (streamEntity.getDeleted() != null) {
          deleteStream(streamEntity);
        } else {
          ContentValues contentValues = streamEntityMapper.toContentValues(streamEntity);
          database.insertWithOnConflict(DatabaseContract.StreamTable.TABLE, null, contentValues,
              SQLiteDatabase.CONFLICT_REPLACE);
        }
      }
      database.setTransactionSuccessful();
    } finally {
      database.endTransaction();
    }
  }

  public void saveStream(StreamEntity streamEntity) {
    if (streamEntity.getDeleted() != null) {
      deleteStream(streamEntity);
    } else {
      ContentValues contentValues = streamEntityMapper.toContentValues(streamEntity);
      getWritableDatabase().insertWithOnConflict(DatabaseContract.StreamTable.TABLE, null,
          contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public long deleteStream(StreamEntity streamEntity) {
    String idEvent = streamEntity.getIdStream();
    return deleteStream(idEvent);
  }

  public long deleteStream(String idEvent) {
    long res = 0;
    String args = DatabaseContract.StreamTable.ID_STREAM + "=?";
    String[] stringArgs = new String[] { String.valueOf(idEvent) };
    Cursor c = getReadableDatabase().query(DatabaseContract.StreamTable.TABLE,
        DatabaseContract.StreamTable.PROJECTION, args, stringArgs, null, null, null);
    if (c.getCount() > 0) {
      res = getWritableDatabase().delete(DatabaseContract.StreamTable.TABLE, args, stringArgs);
    }
    c.close();
    return res;
  }

  public List<StreamSearchEntity> getDefaultStreamSearch() {
    List<StreamSearchEntity> eventSearchEntities = new ArrayList<>();

    Cursor queryResults = getReadableDatabase().query(DatabaseContract.StreamSearchTable.TABLE,
        DatabaseContract.StreamSearchTable.PROJECTION, null, null, null, null, null);

    if (queryResults.getCount() > 0) {
      queryResults.moveToFirst();
      do {
        eventSearchEntities.add(streamEntityMapper.fromSearchCursor(queryResults));
      } while (queryResults.moveToNext());
    }
    return eventSearchEntities;
  }

  public StreamSearchEntity getStreamSearchResultById(String idStream) {
    String whereClause = DatabaseContract.StreamSearchTable.ID_STREAM + "=?";
    String[] whereArguments = new String[] { String.valueOf(idStream) };

    Cursor queryResults = getReadableDatabase().query(DatabaseContract.StreamSearchTable.TABLE,
        DatabaseContract.StreamSearchTable.PROJECTION, whereClause, whereArguments, null, null,
        null, "1");

    if (queryResults.getCount() > 0) {
      queryResults.moveToFirst();
      return streamEntityMapper.fromSearchCursor(queryResults);
    } else {
      return null;
    }
  }

  public List<StreamEntity> getStreamsListingNotRemoved(String idUser) {
    String whereSelection = DatabaseContract.StreamTable.ID_USER
        + " = ? AND "
        + DatabaseContract.StreamTable.REMOVED
        + " = 0";
    String[] whereArguments = new String[] { idUser };

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.StreamTable.TABLE,
        DatabaseContract.StreamTable.PROJECTION, whereSelection, whereArguments, null, null,
        DatabaseContract.StreamTable.TITLE);

    List<StreamEntity> resultEvents = new ArrayList<>(queryResult.getCount());
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      do {
        StreamEntity streamEntity = streamEntityMapper.fromCursor(queryResult);
        resultEvents.add(streamEntity);
      } while (queryResult.moveToNext());
    }

    queryResult.close();
    return resultEvents;
  }

  public Long getLastModifiedDateForStream(String streamId) {
    String whereClause = TimelineSyncTable.STREAM_ID + " = ?";

    String[] whereArguments = new String[] { String.valueOf(streamId) };

    Cursor queryResult =
        getReadableDatabase().query(TimelineSyncTable.TABLE, TimelineSyncTable.PROJECTION,
            whereClause, whereArguments, null, null, null, "1");

    Long resultDate;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      int dateColumnIndex = queryResult.getColumnIndex(TimelineSyncTable.DATE);
      resultDate = queryResult.getLong(dateColumnIndex);
    } else {
      resultDate = 0L;
    }
    queryResult.close();
    return resultDate;
  }

  public void setLastModifiedDateForStream(String streamId, Long refreshDate) {
    ContentValues values = new ContentValues(2);
    values.put(TimelineSyncTable.STREAM_ID, streamId);
    values.put(TimelineSyncTable.DATE, refreshDate);
    getWritableDatabase().insertWithOnConflict(TimelineSyncTable.TABLE, null, values,
        SQLiteDatabase.CONFLICT_REPLACE);
  }

  public void removeStream(String idStream) {
    StreamEntity stream = getStreamById(idStream);
    stream.setRemoved(1);
    saveStream(stream);
  }

  public void restoreStream(String idStream) {
    StreamEntity stream = getStreamById(idStream);
    stream.setRemoved(0);
    saveStream(stream);
  }

  public void saveLastTimeFilteredStream(String idStream, Long lastTimeFiltered) {
    ContentValues values = new ContentValues(2);
    values.put(DatabaseContract.StreamFilterTable.ID_STREAM, idStream);
    values.put(DatabaseContract.StreamFilterTable.LAST_TIME_FILTERED, lastTimeFiltered);
    getWritableDatabase().insertWithOnConflict(DatabaseContract.StreamFilterTable.TABLE, null,
        values, SQLiteDatabase.CONFLICT_REPLACE);
  }

  public void mute(String idStream) {
    muteUnmute(idStream, true);
  }

  public void unMute(String idStream) {
    muteUnmute(idStream, false);
  }

  public void muteStreamSearchResult(String idStream) {
    muteUnmuteStreamSearchResult(idStream, true);
  }

  public void unMuteStreamSearchResult(String idStream) {
    muteUnmuteStreamSearchResult(idStream, false);
  }

  private void muteUnmute(String idStream, boolean mute) {
    String whereClause = DatabaseContract.StreamTable.ID_STREAM + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idStream) };
    ContentValues values = new ContentValues(1);
    values.put(DatabaseContract.StreamTable.MUTED, mute ? 1 : 0);
    getWritableDatabase().update(DatabaseContract.StreamTable.TABLE, values, whereClause,
        whereArguments);
  }

  private void muteUnmuteStreamSearchResult(String idStream, boolean mute) {
    String whereClause = DatabaseContract.StreamSearchTable.ID_STREAM + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idStream) };
    ContentValues values = new ContentValues(1);
    values.put(DatabaseContract.StreamSearchTable.MUTED, mute ? 1 : 0);
    getWritableDatabase().update(DatabaseContract.StreamSearchTable.TABLE, values, whereClause,
        whereArguments);
  }

  private void followUnfollowStreamSearchResult(String idStream, boolean mute) {
    String whereClause = DatabaseContract.StreamSearchTable.ID_STREAM + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idStream) };
    ContentValues values = new ContentValues(1);
    values.put(DatabaseContract.StreamSearchTable.FOLLOWING, mute ? 1 : 0);
    getWritableDatabase().update(DatabaseContract.StreamSearchTable.TABLE, values, whereClause,
        whereArguments);
  }

  public Long getLastTimeFilteredStream(String streamId) {
    String whereClause = DatabaseContract.StreamFilterTable.ID_STREAM + " = ?";

    String[] whereArguments = new String[] { String.valueOf(streamId) };

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.StreamFilterTable.TABLE,
        DatabaseContract.StreamFilterTable.PROJECTION, whereClause, whereArguments, null, null,
        null, "1");

    Long resultDate;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      int dateColumnIndex =
          queryResult.getColumnIndex(DatabaseContract.StreamFilterTable.LAST_TIME_FILTERED);
      resultDate = queryResult.getLong(dateColumnIndex);
    } else {
      resultDate = 0L;
    }
    queryResult.close();
    return resultDate;
  }

  public void followStreamSearchResult(String idStream) {
    followUnfollowStreamSearchResult(idStream, true);
  }

  public void unFollowStreamSearchResult(String idStream) {
    followUnfollowStreamSearchResult(idStream, false);
  }

  public void follow(String idStream) {
    followUnfollow(idStream, true);
  }

  public void unFollow(String idStream) {
    followUnfollow(idStream, false);
  }

  private void followUnfollow(String idStream, boolean following) {
    String whereClause = DatabaseContract.StreamTable.ID_STREAM + " = ?";
    String[] whereArguments = new String[] { String.valueOf(idStream) };
    ContentValues values = new ContentValues(1);
    values.put(DatabaseContract.StreamTable.FOLLOWING, following ? 1 : 0);
    getWritableDatabase().update(DatabaseContract.StreamTable.TABLE, values, whereClause,
        whereArguments);
  }

  public void saveFailedFollow(FollowEntity follow) {
    if (follow != null) {
      ContentValues contentValues = followMapper.toContentValues(follow);
      getWritableDatabase().insertWithOnConflict(FOLLOW_TABLE,
          null,
          contentValues,
          SQLiteDatabase.CONFLICT_REPLACE);
    }
  }

  public List<FollowEntity> getFailedFollows() {
    List<FollowEntity> followsToUpdate = new ArrayList<>();
    String whereClause = TYPE + "= ?";
    String[] whereArgs = new String[] { "STREAM" };
    Cursor c = getReadableDatabase().query(FOLLOW_TABLE,
        DatabaseContract.FollowTable.PROJECTION,
        whereClause,
        whereArgs,
        null,
        null,
        null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      do {
        followsToUpdate.add(followMapper.fromCursor(c));
      } while (c.moveToNext());
    }
    c.close();
    return followsToUpdate;
  }

  public void deleteFailedFollows() {
    String whereClause = TYPE + "=?";
    String[] whereArgs = new String[] { "STREAM" };
    getWritableDatabase().delete(FOLLOW_TABLE, whereClause, whereArgs);
  }

  public long getConnectionTimes(String idStream) {
    String whereClause = DatabaseContract.StreamConnectionsTable.ID_STREAM + " = ?";

    String[] whereArguments = new String[] { String.valueOf(idStream) };

    Cursor queryResult = getReadableDatabase().query(DatabaseContract.StreamConnectionsTable.TABLE,
        DatabaseContract.StreamConnectionsTable.PROJECTION, whereClause, whereArguments, null, null,
        null, null);

    long times;
    if (queryResult.getCount() > 0) {
      queryResult.moveToFirst();
      int dateColumnIndex =
          queryResult.getColumnIndex(DatabaseContract.StreamConnectionsTable.CONNECTION_TIMES);
      times = queryResult.getLong(dateColumnIndex);
    } else {
      times = 1L;
    }
    queryResult.close();
    return times;
  }

  public void storeConnection(String idStream, long connections) {
    ContentValues values = new ContentValues(2);
    values.put(DatabaseContract.StreamConnectionsTable.ID_STREAM, idStream);
    values.put(DatabaseContract.StreamConnectionsTable.CONNECTION_TIMES, connections);
    getWritableDatabase().insertWithOnConflict(DatabaseContract.StreamConnectionsTable.TABLE, null,
        values, SQLiteDatabase.CONFLICT_REPLACE);
  }
}
