package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.PrivateMessageQueueEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class MessageQueueDBMapper extends GenericDBMapper {

  @Inject public MessageQueueDBMapper() {
  }

  public PrivateMessageQueueEntity fromCursor(Cursor cursor) {
    PrivateMessageQueueEntity privateMessageQueueEntity = new PrivateMessageQueueEntity();

    if (!cursor.isNull(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.ID_QUEUE))) {
      privateMessageQueueEntity.setIdQueue(
          cursor.getLong(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.ID_QUEUE)));
    } else {
      privateMessageQueueEntity.setIdQueue(null);
    }
    privateMessageQueueEntity.setFailed(
        cursor.getInt(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.FAILED)));
    privateMessageQueueEntity.setImageFile(
        cursor.getString(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.IMAGE_FILE)));
    if (!cursor.isNull(
        cursor.getColumnIndex(DatabaseContract.MessageQueueTable.ID_PRIVATE_MESSAGE))) {
      privateMessageQueueEntity.setIdPrivateMessage(cursor.getString(
          cursor.getColumnIndex(DatabaseContract.MessageQueueTable.ID_PRIVATE_MESSAGE)));
    }
    privateMessageQueueEntity.setIdTargetUser(
        cursor.getString(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.ID_TARGET_USER)));
    privateMessageQueueEntity.setIdUser(
        cursor.getString(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.ID_USER)));
    privateMessageQueueEntity.setUsername(
        cursor.getString(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.USERNAME)));
    privateMessageQueueEntity.setComment(
        cursor.getString(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.COMMENT)));
    privateMessageQueueEntity.setImage(
        cursor.getString(cursor.getColumnIndex(DatabaseContract.MessageQueueTable.IMAGE)));
    privateMessageQueueEntity.setIdPrivateMessageChannel(cursor.getString(
        cursor.getColumnIndex(DatabaseContract.MessageQueueTable.ID_PRIVATE_MESSAGE_CHANNEL)));

    setSynchronizedfromCursor(cursor, privateMessageQueueEntity);
    return privateMessageQueueEntity;
  }

  public ContentValues toContentValues(PrivateMessageQueueEntity entity) {
    ContentValues contentValues = new ContentValues();

    contentValues.put(DatabaseContract.MessageQueueTable.ID_QUEUE, entity.getIdQueue());
    contentValues.put(DatabaseContract.MessageQueueTable.FAILED, entity.getFailed());
    contentValues.put(DatabaseContract.MessageQueueTable.IMAGE_FILE, entity.getImageFile());
    contentValues.put(DatabaseContract.MessageQueueTable.ID_USER, entity.getIdUser());

    contentValues.put(DatabaseContract.MessageQueueTable.ID_PRIVATE_MESSAGE, entity.getIdPrivateMessage());
    contentValues.put(DatabaseContract.MessageQueueTable.ID_TARGET_USER, entity.getIdTargetUser());
    contentValues.put(DatabaseContract.MessageQueueTable.USERNAME, entity.getUsername());
    contentValues.put(DatabaseContract.MessageQueueTable.COMMENT, entity.getComment());
    contentValues.put(DatabaseContract.MessageQueueTable.IMAGE, entity.getImage());
    contentValues.put(DatabaseContract.MessageQueueTable.ID_PRIVATE_MESSAGE_CHANNEL,
        entity.getIdPrivateMessageChannel());

    setSynchronizedtoContentValues(entity, contentValues);

    return contentValues;
  }
}
