package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.TimelineSyncTable;
import com.shootr.android.db.mappers.StreamEntityDBMapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class StreamManager extends AbstractManager{

    private final StreamEntityDBMapper streamEntityMapper;

    @Inject public StreamManager(SQLiteOpenHelper openHelper, StreamEntityDBMapper streamEntityMapper){
        super(openHelper);
        this.streamEntityMapper = streamEntityMapper;
    }

    public StreamEntity getStreamById(String streamId) {
        String whereSelection = DatabaseContract.StreamTable.ID_STREAM + " = ?";
        String[] whereArguments = new String[] { String.valueOf(streamId) };

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.StreamTable.TABLE, DatabaseContract.StreamTable.PROJECTION, whereSelection, whereArguments, null,
            null, null);

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
          + " IN (" + createListPlaceholders(streamIds.size())+")";
        String[] whereArguments = new String[streamIds.size()];
        for (int i = 0; i < streamIds.size(); i++) {
            whereArguments[i] = streamIds.get(i);
        }

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.StreamTable.TABLE, DatabaseContract.StreamTable.PROJECTION, whereSelection, whereArguments, null, null, null);

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
                    database.insertWithOnConflict(DatabaseContract.StreamTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
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
            getWritableDatabase().insertWithOnConflict(DatabaseContract.StreamTable.TABLE,
              null,
              contentValues,
              SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public long deleteStream(StreamEntity streamEntity){
        String idEvent = streamEntity.getIdStream();
        return deleteStream(idEvent);
    }

    public long deleteStream(String idEvent) {
        long res = 0;
        String args = DatabaseContract.StreamTable.ID_STREAM + "=?";
        String[] stringArgs = new String[]{String.valueOf(idEvent)};
        Cursor c = getReadableDatabase().query(DatabaseContract.StreamTable.TABLE,
          DatabaseContract.StreamTable.PROJECTION,
          args,
          stringArgs,
          null,
          null,
          null);
        if (c.getCount() > 0) {
           res = getWritableDatabase().delete(DatabaseContract.StreamTable.TABLE, args, stringArgs);
        }
        c.close();
        return res;
    }

    public List<StreamSearchEntity> getDefaultStreamSearch() {
        List<StreamSearchEntity> eventSearchEntities = new ArrayList<>();

        Cursor queryResults = getReadableDatabase().query(DatabaseContract.StreamSearchTable.TABLE,
          DatabaseContract.StreamSearchTable.PROJECTION,
          null,
          null,
          null,
          null,
          null);

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
          DatabaseContract.StreamSearchTable.PROJECTION,
          whereClause,
          whereArguments,
          null,
          null,
          null,
          "1");

        if (queryResults.getCount() > 0) {
            queryResults.moveToFirst();
            return streamEntityMapper.fromSearchCursor(queryResults);
        } else {
            return null;
        }
    }

    public void putDefaultStreamSearch(List<StreamSearchEntity> eventSearchEntities) {
        SQLiteDatabase database = getWritableDatabase();
        try{
            database.beginTransaction();
            for (StreamSearchEntity streamSearchEntity : eventSearchEntities) {
                database.insertWithOnConflict(DatabaseContract.StreamSearchTable.TABLE,
                        null,
                        streamEntityMapper.toSearchContentValues(streamSearchEntity),
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }

    }

    public void deleteDefaultStreamSearch() {
        getWritableDatabase().delete(DatabaseContract.StreamSearchTable.TABLE, null, null);
    }

    public Integer getListingNotRemoved(String idUser) {
        String whereSelection = DatabaseContract.StreamTable.ID_USER + " = ? AND " + DatabaseContract.StreamTable.REMOVED + " = 0";
        String[] whereArguments = new String[] { idUser };

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.StreamTable.TABLE, DatabaseContract.StreamTable.PROJECTION, whereSelection, whereArguments, null, null, null);

        int listingCount = queryResult.getCount();

        queryResult.close();
        return listingCount;
    }

    public List<StreamEntity> getStreamsListingNotRemoved(String idUser) {
        String whereSelection = DatabaseContract.StreamTable.ID_USER + " = ? AND " + DatabaseContract.StreamTable.REMOVED + " = 0";
        String[] whereArguments = new String[] { idUser };

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.StreamTable.TABLE, DatabaseContract.StreamTable.PROJECTION, whereSelection, whereArguments, null, null,
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

        String[] whereArguments = new String[]{String.valueOf(streamId)};

        Cursor queryResult = getReadableDatabase().query(TimelineSyncTable.TABLE, TimelineSyncTable.PROJECTION, whereClause, whereArguments, null, null, null, "1");

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
        getWritableDatabase().insertWithOnConflict(TimelineSyncTable.TABLE,
          null,
          values,
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
}
