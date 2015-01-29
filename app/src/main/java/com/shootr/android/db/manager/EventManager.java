package com.shootr.android.db.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import com.shootr.android.data.entity.EventEntity;
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

    public EventEntity getEventById(Long eventid) {
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

    public void saveEvent(EventEntity eventEntity) {
        ContentValues contentValues = eventEntityMapper.toContentValues(eventEntity);
        if (contentValues.getAsLong(DatabaseContract.EventTable.CSYS_DELETED) != null) {
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
        Cursor c = getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, args, stringArgs, null, null, null);
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
        insertInTableSync(DatabaseContract.EventTable.TABLE,10,1000,0);
    }

    public List<EventEntity> getEndedEvents() {
        String whereSelection = DatabaseContract.EventTable.END_DATE + "<" + timeUtils.getCurrentTime();
        Cursor queryResult =
          getReadableDatabase().query(DatabaseContract.EventTable.TABLE, DatabaseContract.EventTable.PROJECTION, whereSelection, null, null, null, null);

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
