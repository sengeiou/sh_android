package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.HighlightedShotEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class HighlightedShotEntityDBMapper extends GenericDBMapper {

  @Inject public HighlightedShotEntityDBMapper() {
  }

  public HighlightedShotEntity fromCursor(Cursor c) {
    HighlightedShotEntity highlightedShot = new HighlightedShotEntity();
    highlightedShot.setIdHighlightedShot(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.ID_HIGHLIGHTED_SHOT)));
    highlightedShot.setActive(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.ACTIVE)));
    highlightedShot.setVisible(c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.VISIBLE)) == 1L);
    highlightedShot.setIdShot(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT)));
    highlightedShot.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_USER)));
    highlightedShot.setUsername(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USERNAME)));
    highlightedShot.setUserPhoto(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USER_PHOTO)));
    highlightedShot.setComment(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.COMMENT)));
    highlightedShot.setImage(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.IMAGE)));
    highlightedShot.setImageWidth(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.IMAGE_WIDTH)));
    highlightedShot.setImageHeight(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.IMAGE_HEIGHT)));
    highlightedShot.setStreamTitle(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.STREAM_TITLE)));
    highlightedShot.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_STREAM)));
    highlightedShot.setType(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.TYPE)));
    highlightedShot.setNiceCount(c.getInt(c.getColumnIndex(DatabaseContract.ShotTable.NICE_COUNT)));
    highlightedShot.setIdShotParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT_PARENT)));
    highlightedShot.setIdUserParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_USER_PARENT)));
    highlightedShot.setUserNameParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USERNAME_PARENT)));
    highlightedShot.setVideoUrl(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_URL)));
    highlightedShot.setVideoTitle(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_TITLE)));
    highlightedShot.setVideoDuration(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_DURATION)));
    highlightedShot.setProfileHidden(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.PROFILE_HIDDEN)));
    highlightedShot.setReplyCount(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.REPLY_COUNT)));
    setSynchronizedfromCursor(c, highlightedShot);
    return highlightedShot;
  }

  public ContentValues toContentValues(HighlightedShotEntity highlightedShot) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(DatabaseContract.HighlightedShotTable.ID_HIGHLIGHTED_SHOT,
        highlightedShot.getIdHighlightedShot());
    contentValues.put(DatabaseContract.HighlightedShotTable.ACTIVE, highlightedShot.getActive());
    Boolean visible = highlightedShot.isVisible() != null ? highlightedShot.isVisible() : true;
    contentValues.put(DatabaseContract.HighlightedShotTable.VISIBLE, visible ? 1L : 0L);
    contentValues.put(DatabaseContract.ShotTable.ID_SHOT, highlightedShot.getIdShot());
    contentValues.put(DatabaseContract.ShotTable.ID_USER, highlightedShot.getIdUser());
    contentValues.put(DatabaseContract.ShotTable.USERNAME, highlightedShot.getUsername());
    contentValues.put(DatabaseContract.ShotTable.USER_PHOTO, highlightedShot.getUserPhoto());
    contentValues.put(DatabaseContract.ShotTable.COMMENT, highlightedShot.getComment());
    contentValues.put(DatabaseContract.ShotTable.IMAGE, highlightedShot.getImage());
    contentValues.put(DatabaseContract.ShotTable.IMAGE_WIDTH, highlightedShot.getImageWidth());
    contentValues.put(DatabaseContract.ShotTable.IMAGE_HEIGHT, highlightedShot.getImageHeight());
    contentValues.put(DatabaseContract.ShotTable.STREAM_TITLE, highlightedShot.getStreamTitle());
    contentValues.put(DatabaseContract.ShotTable.ID_STREAM, highlightedShot.getIdStream());
    contentValues.put(DatabaseContract.ShotTable.TYPE, highlightedShot.getType());
    contentValues.put(DatabaseContract.ShotTable.NICE_COUNT, highlightedShot.getNiceCount());
    contentValues.put(DatabaseContract.ShotTable.ID_SHOT_PARENT, highlightedShot.getIdShotParent());
    contentValues.put(DatabaseContract.ShotTable.ID_USER_PARENT, highlightedShot.getIdUserParent());
    contentValues.put(DatabaseContract.ShotTable.USERNAME_PARENT, highlightedShot.getUserNameParent());
    contentValues.put(DatabaseContract.ShotTable.VIDEO_URL, highlightedShot.getVideoUrl());
    contentValues.put(DatabaseContract.ShotTable.VIDEO_TITLE, highlightedShot.getVideoTitle());
    contentValues.put(DatabaseContract.ShotTable.VIDEO_DURATION, highlightedShot.getVideoDuration());
    contentValues.put(DatabaseContract.ShotTable.PROFILE_HIDDEN, highlightedShot.getProfileHidden());
    contentValues.put(DatabaseContract.ShotTable.REPLY_COUNT, highlightedShot.getReplyCount());
    setSynchronizedtoContentValues(highlightedShot, contentValues);
    return contentValues;
  }
}

