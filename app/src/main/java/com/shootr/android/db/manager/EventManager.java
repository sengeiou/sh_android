package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.mappers.EventEntityMapper;
import com.shootr.android.domain.utils.TimeUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventManager extends AbstractManager{

    private final EventEntityMapper eventEntityMapper;
    private final TimeUtils timeUtils;

    @Inject public EventManager(SQLiteOpenHelper openHelper, EventEntityMapper eventEntityMapper, TimeUtils timeUtils){
        super(openHelper);
        this.eventEntityMapper = eventEntityMapper;
        this.timeUtils = timeUtils;
    }

    public EventEntity getEventById(String eventid) {
        String whereSelection = DatabaseContract.EventTable.ID_EVENT + " = ?";
        String[] whereArguments = new String[] { String.valueOf(eventid) };

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, whereSelection, whereArguments, null,
            null, null);

        EventEntity eventEntity = null;
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            eventEntity = eventEntityMapper.fromCursor(queryResult);
        }
        queryResult.close();
        return eventEntity;
    }

    public List<EventEntity> getEventsByIds(List<String> eventIds) {
        String whereSelection = DatabaseContract.EventTable.ID_EVENT
          + " IN (" + createListPlaceholders(eventIds.size())+")";
        String[] whereArguments = new String[eventIds.size()];
        for (int i = 0; i < eventIds.size(); i++) {
            whereArguments[i] = eventIds.get(i);
        }

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, whereSelection, whereArguments, null, null, null);

        List<EventEntity> resultEvents = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                EventEntity eventEntity = eventEntityMapper.fromCursor(queryResult);
                resultEvents.add(eventEntity);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return resultEvents;
    }

    public void saveEvents(List<EventEntity> eventEntities) {
        SQLiteDatabase database = getWritableDatabase();
        for (EventEntity eventEntity : eventEntities) {
            ContentValues contentValues = eventEntityMapper.toContentValues(eventEntity);
            if (contentValues.getAsLong(DatabaseContract.EventTable.DELETED) != null) {
                deleteEvent(eventEntity);
            } else {
                database.insertWithOnConflict(DatabaseContract.EventTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    public void saveEvent(EventEntity eventEntity) {
        ContentValues contentValues = eventEntityMapper.toContentValues(eventEntity);
        if (contentValues.getAsLong(DatabaseContract.EventTable.DELETED) != null) {
            deleteEvent(eventEntity);
        } else {
            getWritableDatabase().insertWithOnConflict(DatabaseContract.EventTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
        insertInSync();
    }

    public long deleteEvent(EventEntity eventEntity){
        long res = 0;
        String args = DatabaseContract.EventTable.ID_EVENT + "=?";
        String[] stringArgs = new String[]{String.valueOf(eventEntity.getIdEvent())};
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

    public void deleteEvents(List<EventEntity> eventEntities) {
        for (EventEntity eventEntity : eventEntities) {
            deleteEvent(eventEntity);
        }
    }

    public void insertInSync(){
        insertInTableSync(DatabaseContract.EventTable.TABLE, 10, 1000, 0);
    }

    public List<EventSearchEntity> getDefaultEventSearch() {
        List<EventSearchEntity> eventSearchEntities = new ArrayList<>();

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
                eventSearchEntities.add(eventEntityMapper.fromSearchCursor(queryResults));
            } while (queryResults.moveToNext());
        }
        return eventSearchEntities;
    }

    public EventSearchEntity getEventSearchResultById(String idEvent) {
        String whereClause = DatabaseContract.EventSearchTable.ID_EVENT + "=?";
        String[] whereArguments = new String[] { String.valueOf(idEvent) };

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
            return eventEntityMapper.fromSearchCursor(queryResults);
        } else {
            return null;
        }
    }

    public void putDefaultEventSearch(List<EventSearchEntity> eventSearchEntities) {
        SQLiteDatabase database = getWritableDatabase();
        try{
            database.beginTransaction();
            for (EventSearchEntity eventSearchEntity : eventSearchEntities) {
                database.insertWithOnConflict(DatabaseContract.EventSearchTable.TABLE,
                        null,
                        eventEntityMapper.toSearchContentValues(eventSearchEntity),
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }

    }

    public void deleteDefaultEventSearch() {
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

    public List<EventEntity> getEventsListing(String idUser, String locale) {
        String localeSelection = DatabaseContract.EventTable.LOCALE + " = ?";
        String userSelection = DatabaseContract.EventTable.ID_USER + " = ?";

        String whereSelection = userSelection + " AND " + localeSelection;
        String[] whereArguments = new String[] { idUser,locale };

        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, whereSelection, whereArguments, null, null, null);

        List<EventEntity> resultEvents = new ArrayList<>(queryResult.getCount());
        if (queryResult.getCount() > 0) {
            queryResult.moveToFirst();
            do {
                EventEntity eventEntity = eventEntityMapper.fromCursor(queryResult);
                resultEvents.add(eventEntity);
            } while (queryResult.moveToNext());
        }

        queryResult.close();
        return resultEvents;
    }
}
