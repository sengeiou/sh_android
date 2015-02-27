package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.ConditionVariable;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.ShotQueueEntity;
import com.shootr.android.db.DatabaseContract;
import javax.inject.Inject;
import javax.inject.Singleton;

public class ShotQueueCursorMapper extends GenericMapper{

    @Inject public ShotQueueCursorMapper() {
    }

    public ShotQueueEntity fromCursor(Cursor cursor) {
        ShotQueueEntity shotQueueEntity = new ShotQueueEntity();

        shotQueueEntity.setIdQueue(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_QUEUE)));
        shotQueueEntity.setFailed(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.FAILED)));
        shotQueueEntity.setImageFile(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.IMAGE_FILE)));
        
        shotQueueEntity.setIdShot(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_SHOT)));
        shotQueueEntity.setIdUser(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_USER)));
        shotQueueEntity.setComment(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.COMMENT)));
        shotQueueEntity.setImage(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.IMAGE)));
        shotQueueEntity.setEventTag(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.EVENT_TAG)));
        shotQueueEntity.setEventTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.EVENT_TITLE)));
        shotQueueEntity.setIdEvent(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_EVENT)));
        shotQueueEntity.setType(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.TYPE)));
        
        setSynchronizedfromCursor(cursor, shotQueueEntity);
        return shotQueueEntity;
    }

    public ContentValues toContentValues(ShotQueueEntity entity) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseContract.ShotQueueTable.ID_QUEUE, entity.getIdQueue());
        contentValues.put(DatabaseContract.ShotQueueTable.FAILED, entity.getFailed());
        contentValues.put(DatabaseContract.ShotQueueTable.IMAGE_FILE, entity.getImageFile());

        contentValues.put(DatabaseContract.ShotQueueTable.ID_SHOT, entity.getIdShot());
        contentValues.put(DatabaseContract.ShotQueueTable.ID_USER, entity.getIdUser());
        contentValues.put(DatabaseContract.ShotQueueTable.COMMENT, entity.getComment());
        contentValues.put(DatabaseContract.ShotQueueTable.IMAGE, entity.getImage());
        contentValues.put(DatabaseContract.ShotQueueTable.EVENT_TAG, entity.getEventTag());
        contentValues.put(DatabaseContract.ShotQueueTable.EVENT_TITLE, entity.getEventTitle());
        contentValues.put(DatabaseContract.ShotQueueTable.ID_EVENT, entity.getIdEvent());
        contentValues.put(DatabaseContract.ShotQueueTable.TYPE, entity.getType());
        setSynchronizedtoContentValues(entity,contentValues);
        
        return contentValues;
        
    }
}
