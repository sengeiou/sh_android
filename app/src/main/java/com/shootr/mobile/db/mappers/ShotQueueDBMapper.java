package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.ShotQueueEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class ShotQueueDBMapper extends GenericDBMapper {

    @Inject public ShotQueueDBMapper() {
    }

    public ShotQueueEntity fromCursor(Cursor cursor) {
        ShotQueueEntity shotQueueEntity = new ShotQueueEntity();

        if (!cursor.isNull(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_QUEUE))) {
            shotQueueEntity.setIdQueue(cursor.getLong(cursor.getColumnIndex(
              DatabaseContract.ShotQueueTable.ID_QUEUE)));
        } else {
            shotQueueEntity.setIdQueue(null);
        }
        shotQueueEntity.setFailed(cursor.getInt(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.FAILED)));
        shotQueueEntity.setImageFile(cursor.getString(cursor.getColumnIndex(
          DatabaseContract.ShotQueueTable.IMAGE_FILE)));

        if (!cursor.isNull(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_SHOT))) {
            shotQueueEntity.setIdShot(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_SHOT)));
        }
        shotQueueEntity.setIdUser(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_USER)));
        shotQueueEntity.setUsername(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.USERNAME)));
        shotQueueEntity.setComment(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.COMMENT)));
        shotQueueEntity.setImage(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.IMAGE)));
        shotQueueEntity.setStreamShortTitle(cursor.getString(cursor.getColumnIndex(
          DatabaseContract.ShotQueueTable.STREAM_SHORT_TITLE)));
        shotQueueEntity.setStreamTitle(cursor.getString(cursor.getColumnIndex(
          DatabaseContract.ShotQueueTable.STREAM_TITLE)));
        shotQueueEntity.setIdStream(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_STREAM)));
        shotQueueEntity.setType(cursor.getString(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.TYPE)));

        if (!cursor.isNull(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_SHOT_PARENT))) {
            shotQueueEntity.setIdShotParent(cursor.getString(cursor.getColumnIndex(
              DatabaseContract.ShotQueueTable.ID_SHOT_PARENT)));
        }
        if (!cursor.isNull(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.ID_USER_PARENT))) {
            shotQueueEntity.setIdUserParent(cursor.getString(cursor.getColumnIndex(
              DatabaseContract.ShotQueueTable.ID_USER_PARENT)));
        }
        if (!cursor.isNull(cursor.getColumnIndex(DatabaseContract.ShotQueueTable.USERNAME_PARENT))) {
            shotQueueEntity.setUserNameParent(cursor.getString(cursor.getColumnIndex(
              DatabaseContract.ShotQueueTable.USERNAME_PARENT)));
        }

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
        contentValues.put(DatabaseContract.ShotQueueTable.USERNAME, entity.getUsername());
        contentValues.put(DatabaseContract.ShotQueueTable.COMMENT, entity.getComment());
        contentValues.put(DatabaseContract.ShotQueueTable.IMAGE, entity.getImage());
        contentValues.put(DatabaseContract.ShotQueueTable.STREAM_SHORT_TITLE, entity.getStreamShortTitle());
        contentValues.put(DatabaseContract.ShotQueueTable.STREAM_TITLE, entity.getStreamTitle());
        contentValues.put(DatabaseContract.ShotQueueTable.ID_STREAM, entity.getIdStream());
        contentValues.put(DatabaseContract.ShotQueueTable.TYPE, entity.getType());
        contentValues.put(DatabaseContract.ShotQueueTable.ID_SHOT_PARENT, entity.getIdShotParent());
        contentValues.put(DatabaseContract.ShotQueueTable.ID_USER_PARENT, entity.getIdUserParent());
        contentValues.put(DatabaseContract.ShotQueueTable.USERNAME_PARENT, entity.getUserNameParent());
        setSynchronizedtoContentValues(entity, contentValues);

        return contentValues;
    }
}
