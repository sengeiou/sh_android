package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.mappers.StreamEntityMapper;
import com.shootr.android.domain.utils.TimeUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class StreamManager extends AbstractManager{

    private final StreamEntityMapper streamEntityMapper;
    private final TimeUtils timeUtils;

    @Inject public StreamManager(SQLiteOpenHelper openHelper, StreamEntityMapper streamEntityMapper, TimeUtils timeUtils){
        super(openHelper);
        this.streamEntityMapper = streamEntityMapper;
        this.timeUtils = timeUtils;
    }

    public StreamEntity getStreamById(String streamId) {
        String whereSelection = DatabaseContract.EventTable.ID_EVENT + " = ?";
        String[] whereArguments = new String[] { String.valueOf(streamId) };

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, whereSelection, whereArguments, null,
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
        String whereSelection = DatabaseContract.EventTable.ID_EVENT
          + " IN (" + createListPlaceholders(streamIds.size())+")";
        String[] whereArguments = new String[streamIds.size()];
        for (int i = 0; i < streamIds.size(); i++) {
            whereArguments[i] = streamIds.get(i);
        }

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, whereSelection, whereArguments, null, null, null);

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
        for (StreamEntity streamEntity : eventEntities) {
            if (streamEntity.getDeleted() != null) {
                deleteStream(streamEntity);
            } else {
                ContentValues contentValues = streamEntityMapper.toContentValues(streamEntity);
                database.insertWithOnConflict(DatabaseContract.EventTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    public void saveStream(StreamEntity streamEntity) {
        if (streamEntity.getDeleted() != null) {
            deleteStream(streamEntity);
        } else {
            ContentValues contentValues = streamEntityMapper.toContentValues(streamEntity);
            getWritableDatabase().insertWithOnConflict(DatabaseContract.EventTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public long deleteStream(StreamEntity streamEntity){
        String idEvent = streamEntity.getIdEvent();
        return deleteStream(idEvent);
    }

    public long deleteStream(String idEvent) {
        long res = 0;
        String args = DatabaseContract.EventTable.ID_EVENT + "=?";
        String[] stringArgs = new String[]{String.valueOf(idEvent)};
        Cursor c = getReadableDatabase().query(DatabaseContract.EventTable.TABLE,
          DatabaseContract.EventTable.PROJECTION,
          args,
          stringArgs,
          null,
          null,
          null);
        if (c.getCount() > 0) {
           res = getWritableDatabase().delete(DatabaseContract.EventTable.TABLE, args, stringArgs);
        }
        c.close();
        return res;
    }

    public void insertInSync(){
        insertInTableSync(DatabaseContract.EventTable.TABLE, 10, 1000, 0);
    }

    public List<StreamSearchEntity> getDefaultStreamSearch() {
        List<StreamSearchEntity> eventSearchEntities = new ArrayList<>();

        Cursor queryResults = getReadableDatabase().query(DatabaseContract.EventSearchTable.TABLE,
          DatabaseContract.EventSearchTable.PROJECTION,
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
        String whereClause = DatabaseContract.EventSearchTable.ID_EVENT + "=?";
        String[] whereArguments = new String[] { String.valueOf(idStream) };

        Cursor queryResults = getReadableDatabase().query(DatabaseContract.EventSearchTable.TABLE,
          DatabaseContract.EventSearchTable.PROJECTION,
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
                database.insertWithOnConflict(DatabaseContract.EventSearchTable.TABLE,
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
        getWritableDatabase().delete(DatabaseContract.EventSearchTable.TABLE, null, null);
    }

    public Integer getListingCount(String idUser) {
        String whereSelection = DatabaseContract.EventTable.ID_USER + " = ?";
        String[] whereArguments = new String[] { idUser };

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, whereSelection, whereArguments, null, null, null);

        int listingCount = queryResult.getCount();

        queryResult.close();
        return listingCount;
    }

    public List<StreamEntity> getStreamsListing(String idUser) {
        String whereSelection = DatabaseContract.EventTable.ID_USER + " = ?";
        String[] whereArguments = new String[] { idUser };

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, whereSelection, whereArguments, null, null,
            DatabaseContract.EventTable.TITLE);

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
}
