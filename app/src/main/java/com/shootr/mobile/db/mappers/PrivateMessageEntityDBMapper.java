package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.gson.Gson;
import com.shootr.mobile.data.entity.EntitiesEntity;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class PrivateMessageEntityDBMapper extends GenericDBMapper {

  @Inject public PrivateMessageEntityDBMapper() {
  }

  public PrivateMessageEntity fromCursor(Cursor c) {
    PrivateMessageEntity privateMessage = new PrivateMessageEntity();
    privateMessage.setIdPrivateMessage(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageTable.ID_PRIVATE_MESSAGE)));
    privateMessage.setIdPrivateMessageChannel(c.getString(
        c.getColumnIndex(DatabaseContract.PrivateMessageTable.ID_PRIVATE_MESSAGE_CHANNEL)));
    privateMessage.setIdUser(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageTable.ID_USER)));
    privateMessage.setUsername(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageTable.USERNAME)));
    privateMessage.setComment(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageTable.COMMENT)));
    privateMessage.setImage(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageTable.IMAGE)));
    privateMessage.setImageWidth(
        c.getLong(c.getColumnIndex(DatabaseContract.PrivateMessageTable.IMAGE_WIDTH)));
    privateMessage.setImageHeight(
        c.getLong(c.getColumnIndex(DatabaseContract.PrivateMessageTable.IMAGE_HEIGHT)));
    privateMessage.setVideoUrl(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageTable.VIDEO_URL)));
    privateMessage.setVideoTitle(
        c.getString(c.getColumnIndex(DatabaseContract.PrivateMessageTable.VIDEO_TITLE)));
    privateMessage.setVideoDuration(
        c.getLong(c.getColumnIndex(DatabaseContract.PrivateMessageTable.VIDEO_DURATION)));
    privateMessage.setVerifiedUser(
        c.getLong(c.getColumnIndex(DatabaseContract.PrivateMessageTable.VERIFIED_USER)));
    setSynchronizedfromCursor(c, privateMessage);
    retrieveEntities(c, privateMessage);
    return privateMessage;
  }

  private void retrieveEntities(Cursor c, PrivateMessageEntity privateMessage) {
    Gson gson = new Gson();
    String json = c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ENTITIES));
    privateMessage.setEntities(gson.fromJson(json, EntitiesEntity.class));
  }

  public ContentValues toContentValues(PrivateMessageEntity privateMessage) {
    ContentValues cv = new ContentValues();
    cv.put(DatabaseContract.PrivateMessageTable.ID_PRIVATE_MESSAGE,
        privateMessage.getIdPrivateMessage());
    cv.put(DatabaseContract.PrivateMessageTable.ID_PRIVATE_MESSAGE_CHANNEL,
        privateMessage.getIdPrivateMessageChannel());
    cv.put(DatabaseContract.PrivateMessageTable.ID_USER, privateMessage.getIdUser());
    cv.put(DatabaseContract.PrivateMessageTable.USERNAME, privateMessage.getUsername());
    cv.put(DatabaseContract.PrivateMessageTable.COMMENT, privateMessage.getComment());
    cv.put(DatabaseContract.PrivateMessageTable.IMAGE, privateMessage.getImage());
    cv.put(DatabaseContract.PrivateMessageTable.IMAGE_WIDTH, privateMessage.getImageWidth());
    cv.put(DatabaseContract.PrivateMessageTable.IMAGE_HEIGHT, privateMessage.getImageHeight());

    cv.put(DatabaseContract.PrivateMessageTable.VIDEO_URL, privateMessage.getVideoUrl());
    cv.put(DatabaseContract.PrivateMessageTable.VIDEO_TITLE, privateMessage.getVideoTitle());
    cv.put(DatabaseContract.PrivateMessageTable.VIDEO_DURATION, privateMessage.getVideoDuration());

    cv.put(DatabaseContract.PrivateMessageTable.VERIFIED_USER, privateMessage.getVerifiedUser());
    setSynchronizedtoContentValues(privateMessage, cv);
    storeEntities(privateMessage, cv);
    return cv;
  }

  private void storeEntities(PrivateMessageEntity privateMessage, ContentValues cv) {
    Gson gson = new Gson();
    String json = gson.toJson(privateMessage.getEntities());
    cv.put(DatabaseContract.PrivateMessageTable.ENTITIES, json);
  }
}

