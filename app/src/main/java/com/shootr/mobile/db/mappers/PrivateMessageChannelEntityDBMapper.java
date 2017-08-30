package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.domain.model.privateMessageChannel.LastMessageType;
import java.util.Date;
import javax.inject.Inject;

public class PrivateMessageChannelEntityDBMapper extends GenericDBMapper {

  @Inject public PrivateMessageChannelEntityDBMapper() {
  }

  public PrivateMessageChannelEntity fromCursor(Cursor c) {
    PrivateMessageChannelEntity privateMessageChannel = new PrivateMessageChannelEntity();
    privateMessageChannel.setIdPrivateMessageChannel(c.getString(
        c.getColumnIndex(DatabaseContract.PrivateMessageChannelTable.ID_PRIVATE_MESSAGE_CHANNEL)));
    privateMessageChannel.setIdTargetUser(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageChannelTable.ID_TARGET_USER)));
    privateMessageChannel.setTitle(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageChannelTable.TITLE)));
    privateMessageChannel.setImage(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageChannelTable.IMAGE)));
    privateMessageChannel.setRead(
        c.getInt(c.getColumnIndex(DatabaseContract.PrivateMessageChannelTable.READ)) == 1);
    PrivateMessageEntity lastPrivateMessage = new PrivateMessageEntity();
    lastPrivateMessage.setBirth(new Date(c.getLong(
        c.getColumnIndex(DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_TIME))));
    lastPrivateMessage.setComment(c.getString(
        c.getColumnIndex(DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_COMMENT)));
    privateMessageChannel.setLastPrivateMessage(lastPrivateMessage);
    UserEntity userEntity = new UserEntity();
    userEntity.setIdUser(privateMessageChannel.getIdTargetUser());
    userEntity.setMuted(c.getInt(c.getColumnIndex(DatabaseContract.PrivateMessageChannelTable.MUTED)) == 1);
    privateMessageChannel.setTargetUser(userEntity);
    setSynchronizedfromCursor(c, privateMessageChannel);
    return privateMessageChannel;
  }

  public ContentValues toContentValues(PrivateMessageChannelEntity privateMessageChannel) {
    ContentValues cv = new ContentValues();
    cv.put(DatabaseContract.PrivateMessageChannelTable.ID_PRIVATE_MESSAGE_CHANNEL,
        privateMessageChannel.getIdPrivateMessageChannel());
    cv.put(DatabaseContract.PrivateMessageChannelTable.ID_TARGET_USER,
        privateMessageChannel.getIdTargetUser());
    cv.put(DatabaseContract.PrivateMessageChannelTable.TITLE, privateMessageChannel.getTitle());
    cv.put(DatabaseContract.PrivateMessageChannelTable.IMAGE, privateMessageChannel.getImage());
    cv.put(DatabaseContract.PrivateMessageChannelTable.READ,
        privateMessageChannel.getRead() ? 1 : 0);
    cv.put(DatabaseContract.PrivateMessageChannelTable.MUTED,
        privateMessageChannel.getTargetUser().isMuted() ? 1 : 0);
    if (privateMessageChannel.getLastPrivateMessage() != null) {
      cv.put(DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_TIME,
          privateMessageChannel.getLastPrivateMessage().getBirth().getTime());
      if (privateMessageChannel.getLastPrivateMessage().getComment() == null) {
        if (privateMessageChannel.getLastPrivateMessage().getVideoUrl() != null) {
          cv.put(DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_COMMENT, LastMessageType.VIDEO);
        } else if (privateMessageChannel.getLastPrivateMessage().getImage() != null) {
          cv.put(DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_COMMENT, LastMessageType.IMAGE);
        }
      } else {
        cv.put(DatabaseContract.PrivateMessageChannelTable.LAST_MESSAGE_COMMENT,
            privateMessageChannel.getLastPrivateMessage().getComment());
      }
    }

    setSynchronizedtoContentValues(privateMessageChannel, cv);
    return cv;
  }
}

