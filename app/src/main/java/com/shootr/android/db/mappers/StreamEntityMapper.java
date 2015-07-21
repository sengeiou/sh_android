package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.db.DatabaseContract;
import java.util.HashMap;
import java.util.Map;

public class StreamEntityMapper extends GenericMapper{

    public ContentValues toContentValues(StreamEntity streamEntity){
        ContentValues contentValues = new ContentValues();
        fillContentValues(streamEntity, contentValues);
        return contentValues;
    }

    private void fillContentValues(StreamEntity streamEntity, ContentValues contentValues) {
        contentValues.put(DatabaseContract.EventTable.ID_EVENT, streamEntity.getIdEvent());
        contentValues.put(DatabaseContract.EventTable.ID_USER, streamEntity.getIdUser());
        contentValues.put(DatabaseContract.EventTable.USERNAME, streamEntity.getUserName());
        contentValues.put(DatabaseContract.EventTable.TITLE, streamEntity.getTitle());
        contentValues.put(DatabaseContract.EventTable.PHOTO, streamEntity.getPhoto());
        contentValues.put(DatabaseContract.EventTable.TAG, streamEntity.getTag());
        contentValues.put(DatabaseContract.EventTable.LOCALE, streamEntity.getLocale());
        setSynchronizedtoContentValues(streamEntity,contentValues);
    }

    public StreamEntity fromDto(Map<String, Object> dto) {
        StreamEntity streamEntity = new StreamEntity();
        fillStreamEntity(dto, streamEntity);
        return streamEntity;
    }

    private void fillStreamEntity(Map<String, Object> dto, StreamEntity streamEntity) {
        streamEntity.setIdEvent(dto.get(DatabaseContract.EventTable.ID_EVENT) == null ? null
          : ((String) (dto.get(DatabaseContract.EventTable.ID_EVENT))));
        streamEntity.setIdUser(dto.get(DatabaseContract.EventTable.ID_USER) == null ? null
          : ((String) (dto.get(DatabaseContract.EventTable.ID_USER))));
        streamEntity.setTitle((String) dto.get(DatabaseContract.EventTable.TITLE) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.TITLE));
        streamEntity.setUserName((String) dto.get(DatabaseContract.EventTable.USERNAME) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.USERNAME));
        streamEntity.setPhoto((String) dto.get(DatabaseContract.EventTable.PHOTO) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.PHOTO));
        streamEntity.setTag((String) dto.get(DatabaseContract.EventTable.TAG) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.TAG));
        streamEntity.setLocale(dto.get(DatabaseContract.EventTable.LOCALE) == null ? null
                : (String) dto.get(DatabaseContract.EventTable.LOCALE));
        setSynchronizedfromDto(dto, streamEntity);
    }

    public Map<String, Object> toDto(StreamEntity streamEntity) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(DatabaseContract.EventTable.ID_EVENT, streamEntity == null ? null : streamEntity.getIdEvent());
        dto.put(DatabaseContract.EventTable.ID_USER, streamEntity == null ? null : streamEntity.getIdUser());
        dto.put(DatabaseContract.EventTable.USERNAME, streamEntity == null ? null : streamEntity.getUserName());
        dto.put(DatabaseContract.EventTable.TITLE, streamEntity == null ? null : streamEntity.getTitle());
        dto.put(DatabaseContract.EventTable.PHOTO, streamEntity == null ? null : streamEntity.getPhoto());
        dto.put(DatabaseContract.EventTable.TAG, streamEntity == null ? null : streamEntity.getTag());
        dto.put(DatabaseContract.EventTable.LOCALE, streamEntity == null ? null : streamEntity.getLocale());
        dto.put(DatabaseContract.EventTable.NOTIFY_CREATION,
          streamEntity == null ? null : streamEntity.getNotifyCreation());
        setSynchronizedtoDto(streamEntity, dto);
        return dto;
    }

    public StreamEntity fromCursor(Cursor c) {
        StreamEntity streamEntity = new StreamEntity();
        fillStreamEntity(c, streamEntity);
        return streamEntity;
    }

    private void fillStreamEntity(Cursor c, StreamEntity streamEntity) {
        streamEntity.setIdEvent(c.getString(c.getColumnIndex(DatabaseContract.EventTable.ID_EVENT)));
        streamEntity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.EventTable.ID_USER)));
        streamEntity.setUserName(c.getString(c.getColumnIndex(DatabaseContract.EventTable.USERNAME)));
        streamEntity.setTitle(c.getString(c.getColumnIndex(DatabaseContract.EventTable.TITLE)));
        streamEntity.setPhoto(c.getString(c.getColumnIndex(DatabaseContract.EventTable.PHOTO)));
        streamEntity.setTag(c.getString(c.getColumnIndex(DatabaseContract.EventTable.TAG)));
        streamEntity.setLocale(c.getString(c.getColumnIndex(DatabaseContract.EventTable.LOCALE)));
        setSynchronizedfromCursor(c, streamEntity);
    }

    public StreamSearchEntity fromSearchDto(Map<String, Object> dataItem) {
        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        fillStreamEntity(dataItem, streamSearchEntity);
        streamSearchEntity.setWatchers(((Number) dataItem.get(DatabaseContract.EventSearchTable.WATCHERS)).intValue());
        return streamSearchEntity;
    }

    public StreamSearchEntity fromSearchCursor(Cursor cursor) {
        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        fillStreamEntity(cursor, streamSearchEntity);
        streamSearchEntity.setWatchers(cursor.getInt(cursor.getColumnIndex(DatabaseContract.EventSearchTable.WATCHERS)));
        return streamSearchEntity;
    }

    public ContentValues toSearchContentValues(StreamSearchEntity entity) {
        ContentValues contentValues = new ContentValues();
        fillContentValues(entity, contentValues);
        contentValues.put(DatabaseContract.EventSearchTable.WATCHERS, entity.getWatchers());
        return contentValues;
    }

}
