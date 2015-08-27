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
        contentValues.put(DatabaseContract.StreamTable.ID_STREAM, streamEntity.getIdStream());
        contentValues.put(DatabaseContract.StreamTable.ID_USER, streamEntity.getIdUser());
        contentValues.put(DatabaseContract.StreamTable.USERNAME, streamEntity.getUserName());
        contentValues.put(DatabaseContract.StreamTable.TITLE, streamEntity.getTitle());
        contentValues.put(DatabaseContract.StreamTable.PHOTO, streamEntity.getPhoto());
        contentValues.put(DatabaseContract.StreamTable.TAG, streamEntity.getTag());
        contentValues.put(DatabaseContract.StreamTable.DESCRIPTION, streamEntity.getDescription());
        contentValues.put(DatabaseContract.StreamTable.LOCALE, streamEntity.getLocale());
        contentValues.put(DatabaseContract.StreamTable.MEDIA_COUNT, streamEntity.getMediaCountByRelatedUsers());
        contentValues.put(DatabaseContract.StreamTable.REMOVED, streamEntity.getRemoved());
        setSynchronizedtoContentValues(streamEntity,contentValues);
    }

    public StreamEntity fromDto(Map<String, Object> dto) {
        StreamEntity streamEntity = new StreamEntity();
        fillStreamEntity(dto, streamEntity);
        return streamEntity;
    }

    private void fillStreamEntity(Map<String, Object> dto, StreamEntity streamEntity) {
        streamEntity.setIdStream(dto.get(DatabaseContract.StreamTable.ID_STREAM) == null ? null
          : ((String) (dto.get(DatabaseContract.StreamTable.ID_STREAM))));
        streamEntity.setIdUser(dto.get(DatabaseContract.StreamTable.ID_USER) == null ? null
          : ((String) (dto.get(DatabaseContract.StreamTable.ID_USER))));
        streamEntity.setTitle(dto.get(DatabaseContract.StreamTable.TITLE) == null ? null
          : (String) dto.get(DatabaseContract.StreamTable.TITLE));
        streamEntity.setUserName(dto.get(DatabaseContract.StreamTable.USERNAME) == null ? null
          : (String) dto.get(DatabaseContract.StreamTable.USERNAME));
        streamEntity.setPhoto(dto.get(DatabaseContract.StreamTable.PHOTO) == null ? null
          : (String) dto.get(DatabaseContract.StreamTable.PHOTO));
        streamEntity.setTag(dto.get(DatabaseContract.StreamTable.TAG) == null ? null
          : (String) dto.get(DatabaseContract.StreamTable.TAG));
        streamEntity.setDescription(dto.get(DatabaseContract.StreamTable.DESCRIPTION) == null ? null
          : (String) dto.get(DatabaseContract.StreamTable.DESCRIPTION));
        streamEntity.setLocale(dto.get(DatabaseContract.StreamTable.LOCALE) == null ? null
                : (String) dto.get(DatabaseContract.StreamTable.LOCALE));
        streamEntity.setRemoved(dto.get(DatabaseContract.StreamTable.REMOVED) == null ? 0
                : (Integer) dto.get(DatabaseContract.StreamTable.REMOVED));
        setSynchronizedfromDto(dto, streamEntity);
    }

    public Map<String, Object> toDto(StreamEntity streamEntity) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(DatabaseContract.StreamTable.ID_STREAM, streamEntity == null ? null : streamEntity.getIdStream());
        dto.put(DatabaseContract.StreamTable.ID_USER, streamEntity == null ? null : streamEntity.getIdUser());
        dto.put(DatabaseContract.StreamTable.USERNAME, streamEntity == null ? null : streamEntity.getUserName());
        dto.put(DatabaseContract.StreamTable.TITLE, streamEntity == null ? null : streamEntity.getTitle());
        dto.put(DatabaseContract.StreamTable.PHOTO, streamEntity == null ? null : streamEntity.getPhoto());
        dto.put(DatabaseContract.StreamTable.TAG, streamEntity == null ? null : streamEntity.getTag());
        dto.put(DatabaseContract.StreamTable.DESCRIPTION, streamEntity == null ? null : streamEntity.getDescription());
        dto.put(DatabaseContract.StreamTable.LOCALE, streamEntity == null ? null : streamEntity.getLocale());
        dto.put(DatabaseContract.StreamTable.NOTIFY_CREATION,
          streamEntity == null ? null : streamEntity.getNotifyCreation());
        dto.put(DatabaseContract.StreamTable.REMOVED, streamEntity == null ? 0 : streamEntity.getRemoved());
        setSynchronizedtoDto(streamEntity, dto);
        return dto;
    }

    public StreamEntity fromCursor(Cursor c) {
        StreamEntity streamEntity = new StreamEntity();
        fillStreamEntity(c, streamEntity);
        return streamEntity;
    }

    private void fillStreamEntity(Cursor c, StreamEntity streamEntity) {
        streamEntity.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.ID_STREAM)));
        streamEntity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.ID_USER)));
        streamEntity.setUserName(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.USERNAME)));
        streamEntity.setTitle(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.TITLE)));
        streamEntity.setPhoto(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.PHOTO)));
        streamEntity.setTag(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.TAG)));
        streamEntity.setDescription(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.DESCRIPTION)));
        streamEntity.setLocale(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.LOCALE)));
        streamEntity.setMediaCountByRelatedUsers(c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.MEDIA_COUNT)));
        streamEntity.setRemoved(c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.REMOVED)));
        setSynchronizedfromCursor(c, streamEntity);
    }

    public StreamSearchEntity fromSearchDto(Map<String, Object> dataItem) {
        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        fillStreamEntity(dataItem, streamSearchEntity);
        streamSearchEntity.setTotalWatchers(((Number) dataItem.get(DatabaseContract.StreamSearchTable.WATCHERS)).intValue());
        return streamSearchEntity;
    }

    public StreamSearchEntity fromSearchCursor(Cursor cursor) {
        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        fillStreamEntity(cursor, streamSearchEntity);
        streamSearchEntity.setTotalWatchers(cursor.getInt(cursor.getColumnIndex(DatabaseContract.StreamSearchTable.WATCHERS)));
        return streamSearchEntity;
    }

    public ContentValues toSearchContentValues(StreamSearchEntity entity) {
        ContentValues contentValues = new ContentValues();
        fillContentValues(entity, contentValues);
        contentValues.put(DatabaseContract.StreamSearchTable.WATCHERS, entity.getTotalWatchers());
        return contentValues;
    }

}
