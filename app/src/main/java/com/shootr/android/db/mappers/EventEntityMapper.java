package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.db.DatabaseContract;
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
        contentValues.put(DatabaseContract.EventTable.TITLE, eventEntity.getTitle());
        contentValues.put(DatabaseContract.EventTable.PHOTO, eventEntity.getPhoto());
        contentValues.put(DatabaseContract.EventTable.TAG, eventEntity.getTag());
        contentValues.put(DatabaseContract.EventTable.LOCALE, eventEntity.getLocale());
        setSynchronizedtoContentValues(eventEntity,contentValues);
    }

    public EventEntity fromDto(Map<String, Object> dto) {
        EventEntity eventEntity = new EventEntity();
        fillEventEntity(dto, eventEntity);
        return eventEntity;
    }

    private void fillEventEntity(Map<String, Object> dto, EventEntity eventEntity) {
        eventEntity.setIdEvent(dto.get(DatabaseContract.EventTable.ID_EVENT) == null ? null
          : ((String) (dto.get(DatabaseContract.EventTable.ID_EVENT))));
        eventEntity.setIdUser(dto.get(DatabaseContract.EventTable.ID_USER) == null ? null
          : ((String) (dto.get(DatabaseContract.EventTable.ID_USER))));
        eventEntity.setTitle((String) dto.get(DatabaseContract.EventTable.TITLE) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.TITLE));
        eventEntity.setUserName((String) dto.get(DatabaseContract.EventTable.USERNAME) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.USERNAME));
        eventEntity.setPhoto((String) dto.get(DatabaseContract.EventTable.PHOTO) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.PHOTO));
        eventEntity.setTag((String) dto.get(DatabaseContract.EventTable.TAG) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.TAG));
        eventEntity.setLocale(dto.get(DatabaseContract.EventTable.LOCALE) == null ? null
                : (String) dto.get(DatabaseContract.EventTable.LOCALE));
        setSynchronizedfromDto(dto,eventEntity);
    }

    public Map<String, Object> toDto(EventEntity eventEntity) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(DatabaseContract.EventTable.ID_EVENT, eventEntity == null ? null : eventEntity.getIdEvent());
        dto.put(DatabaseContract.EventTable.ID_USER, eventEntity == null ? null : eventEntity.getIdUser());
        dto.put(DatabaseContract.EventTable.USERNAME, eventEntity == null ? null : eventEntity.getUserName());
        dto.put(DatabaseContract.EventTable.TITLE, eventEntity == null ? null : eventEntity.getTitle());
        dto.put(DatabaseContract.EventTable.PHOTO, eventEntity == null ? null : eventEntity.getPhoto());
        dto.put(DatabaseContract.EventTable.TAG, eventEntity == null ? null : eventEntity.getTag());
        dto.put(DatabaseContract.EventTable.LOCALE, eventEntity == null ? null : eventEntity.getLocale());
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
        eventEntity.setIdEvent(c.getString(c.getColumnIndex(DatabaseContract.EventTable.ID_EVENT)));
        eventEntity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.EventTable.ID_USER)));
        eventEntity.setUserName(c.getString(c.getColumnIndex(DatabaseContract.EventTable.USERNAME)));
        eventEntity.setTitle(c.getString(c.getColumnIndex(DatabaseContract.EventTable.TITLE)));
        eventEntity.setPhoto(c.getString(c.getColumnIndex(DatabaseContract.EventTable.PHOTO)));
        eventEntity.setTag(c.getString(c.getColumnIndex(DatabaseContract.EventTable.TAG)));
        eventEntity.setLocale(c.getString(c.getColumnIndex(DatabaseContract.EventTable.LOCALE)));
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
