package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.db.DatabaseContract;

public class StreamEntityDBMapper extends GenericDBMapper {

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
