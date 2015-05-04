package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.db.DatabaseContract;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventEntityMapper extends GenericMapper{

    public ContentValues toContentValues(EventEntity eventEntity){
        ContentValues contentValues = new ContentValues();
        fillContentValues(eventEntity, contentValues);
        return contentValues;
    }

    private void fillContentValues(EventEntity eventEntity, ContentValues contentValues) {
        contentValues.put(DatabaseContract.EventTable.ID_EVENT, eventEntity.getIdEvent());
        contentValues.put(DatabaseContract.EventTable.ID_USER, eventEntity.getIdUser());
        contentValues.put(DatabaseContract.EventTable.USERNAME, eventEntity.getUserName());
        contentValues.put(DatabaseContract.EventTable.BEGIN_DATE, eventEntity.getBeginDate()!=null ? eventEntity.getBeginDate().getTime() : null);
        contentValues.put(DatabaseContract.EventTable.END_DATE, eventEntity.getEndDate()!=null ? eventEntity.getEndDate().getTime() : null);
        contentValues.put(DatabaseContract.EventTable.ID_LOCAL_TEAM, eventEntity.getIdLocalTeam());
        contentValues.put(DatabaseContract.EventTable.ID_VISITOR_TEAM, eventEntity.getIdVisitorTeam());
        contentValues.put(DatabaseContract.EventTable.TITLE, eventEntity.getTitle());
        contentValues.put(DatabaseContract.EventTable.PHOTO, eventEntity.getPhoto());
        contentValues.put(DatabaseContract.EventTable.TIMEZONE, eventEntity.getTimezone());
        contentValues.put(DatabaseContract.EventTable.TAG, eventEntity.getTag());
        setSynchronizedtoContentValues(eventEntity,contentValues);
    }

    public EventEntity fromDto(Map<String, Object> dto) {
        EventEntity eventEntity = new EventEntity();
        fillEventEntity(dto, eventEntity);
        return eventEntity;
    }

    private void fillEventEntity(Map<String, Object> dto, EventEntity eventEntity) {
        eventEntity.setIdEvent((Number) dto.get(DatabaseContract.EventTable.ID_EVENT) == null ? null
          : ((Number) dto.get(DatabaseContract.EventTable.ID_EVENT)).longValue());
        eventEntity.setIdUser((Number) dto.get(DatabaseContract.EventTable.ID_USER) == null ? null
          : ((Number) dto.get(DatabaseContract.EventTable.ID_USER)).longValue());
        eventEntity.setIdLocalTeam((Number) dto.get(DatabaseContract.EventTable.ID_LOCAL_TEAM) == null ? null
          : ((Number) dto.get(DatabaseContract.EventTable.ID_LOCAL_TEAM)).longValue());
        eventEntity.setIdVisitorTeam((Number) dto.get(DatabaseContract.EventTable.ID_VISITOR_TEAM) == null ? null
          : ((Number) dto.get(DatabaseContract.EventTable.ID_VISITOR_TEAM)).longValue());
        eventEntity.setTitle((String) dto.get(DatabaseContract.EventTable.TITLE) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.TITLE));
        eventEntity.setUserName((String) dto.get(DatabaseContract.EventTable.USERNAME) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.USERNAME));
        eventEntity.setPhoto((String) dto.get(DatabaseContract.EventTable.PHOTO) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.PHOTO));
        eventEntity.setTimezone((String) dto.get(DatabaseContract.EventTable.TIMEZONE) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.TIMEZONE));
        eventEntity.setTag((String) dto.get(DatabaseContract.EventTable.TAG) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.TAG));
        eventEntity.setBeginDate(dto.get(DatabaseContract.EventTable.BEGIN_DATE) == null ? null
          : new Date((Long) dto.get(DatabaseContract.EventTable.BEGIN_DATE)));
        eventEntity.setEndDate(dto.get(DatabaseContract.EventTable.END_DATE) == null ? null
          : new Date((Long) dto.get(DatabaseContract.EventTable.END_DATE)));
        setSynchronizedfromDto(dto, eventEntity);
    }

    public  Map<String, Object> toDto(EventEntity eventEntity) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(DatabaseContract.EventTable.ID_EVENT, eventEntity == null ? null : eventEntity.getIdEvent());
        dto.put(DatabaseContract.EventTable.ID_USER, eventEntity == null ? null : eventEntity.getIdUser());
        dto.put(DatabaseContract.EventTable.USERNAME, eventEntity == null ? null : eventEntity.getUserName());
        dto.put(DatabaseContract.EventTable.ID_LOCAL_TEAM, eventEntity == null ? null : eventEntity.getIdLocalTeam());
        dto.put(DatabaseContract.EventTable.ID_VISITOR_TEAM,
          eventEntity == null ? null : eventEntity.getIdVisitorTeam());
        dto.put(DatabaseContract.EventTable.BEGIN_DATE, eventEntity == null ? null : eventEntity.getBeginDate());
        dto.put(DatabaseContract.EventTable.END_DATE, eventEntity == null ? null : eventEntity.getEndDate());
        dto.put(DatabaseContract.EventTable.TITLE, eventEntity == null ? null : eventEntity.getTitle());
        dto.put(DatabaseContract.EventTable.PHOTO, eventEntity == null ? null : eventEntity.getPhoto());
        dto.put(DatabaseContract.EventTable.TIMEZONE, eventEntity == null ? null : eventEntity.getTimezone());
        dto.put(DatabaseContract.EventTable.TAG, eventEntity == null ? null : eventEntity.getTag());
        dto.put(DatabaseContract.EventTable.NOTIFY_CREATION,
          eventEntity == null ? null : eventEntity.getNotifyCreation());
        setSynchronizedtoDto(eventEntity, dto);
        return dto;
    }

    public EventEntity fromCursor(Cursor c) {
        EventEntity eventEntity = new EventEntity();
        fillEventEntity(c, eventEntity);
        return eventEntity;
    }

    private void fillEventEntity(Cursor c, EventEntity eventEntity) {
        eventEntity.setIdEvent(c.getLong(c.getColumnIndex(DatabaseContract.EventTable.ID_EVENT)));
        eventEntity.setIdUser(c.getLong(c.getColumnIndex(DatabaseContract.EventTable.ID_USER)));
        eventEntity.setUserName(c.getString(c.getColumnIndex(DatabaseContract.EventTable.USERNAME)));
        eventEntity.setIdLocalTeam(c.getLong(c.getColumnIndex(DatabaseContract.EventTable.ID_LOCAL_TEAM)));
        eventEntity.setIdVisitorTeam(c.getLong(c.getColumnIndex(DatabaseContract.EventTable.ID_VISITOR_TEAM)));
        eventEntity.setTitle(c.getString(c.getColumnIndex(DatabaseContract.EventTable.TITLE)));
        eventEntity.setPhoto(c.getString(c.getColumnIndex(DatabaseContract.EventTable.PHOTO)));
        eventEntity.setTimezone(c.getString(c.getColumnIndex(DatabaseContract.EventTable.TIMEZONE)));
        eventEntity.setTag(c.getString(c.getColumnIndex(DatabaseContract.EventTable.TAG)));
        Long date = c.getLong(c.getColumnIndex(DatabaseContract.EventTable.BEGIN_DATE));
        eventEntity.setBeginDate(date != 0L ? new Date(date) : null);
        date = c.getLong(c.getColumnIndex(DatabaseContract.EventTable.END_DATE));
        eventEntity.setEndDate(date != 0L ? new Date(date) : null);
        setSynchronizedfromCursor(c, eventEntity);
    }

    public EventSearchEntity fromSearchDto(Map<String, Object> dataItem) {
        EventSearchEntity eventSearchEntity = new EventSearchEntity();
        fillEventEntity(dataItem, eventSearchEntity);
        eventSearchEntity.setWatchers(((Number) dataItem.get(DatabaseContract.EventSearchTable.WATCHERS)).intValue());
        return eventSearchEntity;
    }

    public EventSearchEntity fromSearchCursor(Cursor cursor) {
        EventSearchEntity eventSearchEntity = new EventSearchEntity();
        fillEventEntity(cursor, eventSearchEntity);
        eventSearchEntity.setWatchers(cursor.getInt(cursor.getColumnIndex(DatabaseContract.EventSearchTable.WATCHERS)));
        return eventSearchEntity;
    }

    public ContentValues toSearchContentValues(EventSearchEntity entity) {
        ContentValues contentValues = new ContentValues();
        fillContentValues(entity, contentValues);
        contentValues.put(DatabaseContract.EventSearchTable.WATCHERS, entity.getWatchers());
        return contentValues;
    }

}
