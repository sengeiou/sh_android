package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.StreamSearchEntity;
import com.shootr.mobile.db.DatabaseContract;

public class StreamEntityDBMapper extends GenericDBMapper {

    public ContentValues toContentValues(StreamEntity streamEntity) {
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
        contentValues.put(DatabaseContract.StreamTable.LANDSCAPE_PHOTO, streamEntity.getLandscapePhoto());
        contentValues.put(DatabaseContract.StreamTable.DESCRIPTION, streamEntity.getDescription());
        contentValues.put(DatabaseContract.StreamTable.TOPIC, streamEntity.getTopic());
        contentValues.put(DatabaseContract.StreamTable.COUNTRY, streamEntity.getCountry());
        contentValues.put(DatabaseContract.StreamTable.MEDIA_COUNT, streamEntity.getMediaCountByRelatedUsers());
        contentValues.put(DatabaseContract.StreamTable.REMOVED, streamEntity.getRemoved());
        contentValues.put(DatabaseContract.StreamTable.TOTAL_FAVORITES, streamEntity.getTotalFavorites());
        contentValues.put(DatabaseContract.StreamTable.TOTAL_WATCHERS, streamEntity.getTotalWatchers());
        contentValues.put(DatabaseContract.StreamTable.HISTORIC_WATCHERS, streamEntity.getHistoricWatchers());
        contentValues.put(DatabaseContract.StreamTable.TOTAL_SHOTS, streamEntity.getTotalShots());
        contentValues.put(DatabaseContract.StreamTable.UNIQUE_SHOTS, streamEntity.getUniqueShots());
        contentValues.put(DatabaseContract.StreamTable.READ_WRITE_MODE, streamEntity.getReadWriteMode());
        setSynchronizedtoContentValues(streamEntity, contentValues);
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
        streamEntity.setLandscapePhoto(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.LANDSCAPE_PHOTO)));
        streamEntity.setDescription(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.DESCRIPTION)));
        streamEntity.setTopic(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.TOPIC)));
        streamEntity.setCountry(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.COUNTRY)));
        streamEntity.setMediaCountByRelatedUsers(c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.MEDIA_COUNT)));
        streamEntity.setRemoved(c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.REMOVED)));
        streamEntity.setTotalFavorites(c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.TOTAL_FAVORITES)));
        streamEntity.setTotalWatchers(c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.TOTAL_WATCHERS)));
        streamEntity.setHistoricWatchers(c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.HISTORIC_WATCHERS)));
        streamEntity.setTotalShots(c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.TOTAL_SHOTS)));
        streamEntity.setUniqueShots(c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.UNIQUE_SHOTS)));
        streamEntity.setReadWriteMode(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.READ_WRITE_MODE)));
        setSynchronizedfromCursor(c, streamEntity);
    }

    public StreamSearchEntity fromSearchCursor(Cursor cursor) {
        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        fillStreamEntity(cursor, streamSearchEntity);
        streamSearchEntity.setTotalFollowingWatchers(cursor
          .getInt(cursor.getColumnIndex(DatabaseContract.StreamSearchTable.WATCHERS)));
        return streamSearchEntity;
    }

    public ContentValues toSearchContentValues(StreamSearchEntity entity) {
        ContentValues contentValues = new ContentValues();
        fillContentValues(entity, contentValues);
        contentValues.put(DatabaseContract.StreamSearchTable.WATCHERS, entity.getTotalFollowingWatchers());
        return contentValues;
    }
}
