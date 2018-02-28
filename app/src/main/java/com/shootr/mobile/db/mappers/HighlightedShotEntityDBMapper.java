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
    highlightedShot.setVisible(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.VISIBLE)) == 1L);
    highlightedShot.setIdShot(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.ID_SHOT)));
    highlightedShot.setIdUser(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.ID_USER)));
    highlightedShot.setUsername(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.USERNAME)));
    highlightedShot.setUserPhoto(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.USER_PHOTO)));
    highlightedShot.setComment(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.COMMENT)));
    highlightedShot.setImage(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.IMAGE)));
    highlightedShot.setImageWidth(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.IMAGE_WIDTH)));
    highlightedShot.setImageHeight(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.IMAGE_HEIGHT)));
    highlightedShot.setStreamTitle(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.STREAM_TITLE)));
    highlightedShot.setIdStream(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.ID_STREAM)));
    highlightedShot.setType(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.TYPE)));
    highlightedShot.setNiceCount(
        c.getInt(c.getColumnIndex(DatabaseContract.HighlightedShotTable.NICE_COUNT)));
    highlightedShot.setIdShotParent(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.ID_SHOT_PARENT)));
    highlightedShot.setIdUserParent(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.ID_USER_PARENT)));
    highlightedShot.setUserNameParent(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.USERNAME_PARENT)));
    highlightedShot.setVideoUrl(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.VIDEO_URL)));
    highlightedShot.setVideoTitle(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.VIDEO_TITLE)));
    highlightedShot.setVideoDuration(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.VIDEO_DURATION)));
    highlightedShot.setViews(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.VIEWS)));
    highlightedShot.setLinkClicks(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.LINK_CLICKS)));
    highlightedShot.setProfileHidden(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.PROFILE_HIDDEN)));
    highlightedShot.setReplyCount(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.REPLY_COUNT)));
    highlightedShot.setReshootCounter(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.RESHOOT_COUNT)));
    highlightedShot.setCtaButtonLink(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.CTA_BUTTON_LINK)));
    highlightedShot.setCtaButtonText(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.CTA_BUTTON_TEXT)));
    highlightedShot.setCtaCaption(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.CTA_CAPTION)));
    highlightedShot.setPromoted(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.PROMOTED)));
    highlightedShot.setVerifiedUser(
        c.getLong(c.getColumnIndex(DatabaseContract.HighlightedShotTable.VERIFIED_USER)));
    highlightedShot.setPadding(
        (c.getInt(c.getColumnIndex(DatabaseContract.HighlightedShotTable.IS_PADDING))));
    highlightedShot.setShareLink(
        c.getString(c.getColumnIndex(DatabaseContract.HighlightedShotTable.SHARE_LINK)));
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
    contentValues.put(DatabaseContract.HighlightedShotTable.ID_SHOT, highlightedShot.getIdShot());
    contentValues.put(DatabaseContract.HighlightedShotTable.ID_USER, highlightedShot.getIdUser());
    contentValues.put(DatabaseContract.HighlightedShotTable.USERNAME,
        highlightedShot.getUsername());
    contentValues.put(DatabaseContract.HighlightedShotTable.USER_PHOTO,
        highlightedShot.getUserPhoto());
    contentValues.put(DatabaseContract.HighlightedShotTable.COMMENT, highlightedShot.getComment());
    contentValues.put(DatabaseContract.HighlightedShotTable.IMAGE, highlightedShot.getImage());
    contentValues.put(DatabaseContract.HighlightedShotTable.IMAGE_WIDTH,
        highlightedShot.getImageWidth());
    contentValues.put(DatabaseContract.HighlightedShotTable.IMAGE_HEIGHT,
        highlightedShot.getImageHeight());
    contentValues.put(DatabaseContract.HighlightedShotTable.STREAM_TITLE,
        highlightedShot.getStreamTitle());
    contentValues.put(DatabaseContract.HighlightedShotTable.ID_STREAM,
        highlightedShot.getIdStream());
    contentValues.put(DatabaseContract.HighlightedShotTable.TYPE, highlightedShot.getType());
    contentValues.put(DatabaseContract.HighlightedShotTable.NICE_COUNT,
        highlightedShot.getNiceCount());
    contentValues.put(DatabaseContract.HighlightedShotTable.ID_SHOT_PARENT,
        highlightedShot.getIdShotParent());
    contentValues.put(DatabaseContract.HighlightedShotTable.ID_USER_PARENT,
        highlightedShot.getIdUserParent());
    contentValues.put(DatabaseContract.HighlightedShotTable.USERNAME_PARENT,
        highlightedShot.getUserNameParent());
    contentValues.put(DatabaseContract.HighlightedShotTable.VIDEO_URL,
        highlightedShot.getVideoUrl());
    contentValues.put(DatabaseContract.HighlightedShotTable.VIDEO_TITLE,
        highlightedShot.getVideoTitle());
    contentValues.put(DatabaseContract.HighlightedShotTable.VIDEO_DURATION,
        highlightedShot.getVideoDuration());
    contentValues.put(DatabaseContract.HighlightedShotTable.PROFILE_HIDDEN,
        highlightedShot.getProfileHidden());
    contentValues.put(DatabaseContract.HighlightedShotTable.REPLY_COUNT,
        highlightedShot.getReplyCount());
    contentValues.put(DatabaseContract.HighlightedShotTable.VIEWS, highlightedShot.getViews());
    contentValues.put(DatabaseContract.HighlightedShotTable.LINK_CLICKS,
        highlightedShot.getLinkClicks());
    contentValues.put(DatabaseContract.HighlightedShotTable.RESHOOT_COUNT,
        highlightedShot.getReshootCounter());
    contentValues.put(DatabaseContract.HighlightedShotTable.PROMOTED,
        highlightedShot.getPromoted());
    contentValues.put(DatabaseContract.HighlightedShotTable.CTA_BUTTON_LINK,
        highlightedShot.getCtaButtonLink());
    contentValues.put(DatabaseContract.HighlightedShotTable.CTA_BUTTON_TEXT,
        highlightedShot.getCtaButtonText());
    contentValues.put(DatabaseContract.HighlightedShotTable.CTA_CAPTION,
        highlightedShot.getCtaCaption());
    contentValues.put(DatabaseContract.HighlightedShotTable.VERIFIED_USER,
        highlightedShot.getVerifiedUser());
    contentValues.put(DatabaseContract.HighlightedShotTable.IS_PADDING,
        highlightedShot.isPadding());
    contentValues.put(DatabaseContract.HighlightedShotTable.SHARE_LINK,
        highlightedShot.getShareLink());
    setSynchronizedtoContentValues(highlightedShot, contentValues);
    return contentValues;
  }
}

