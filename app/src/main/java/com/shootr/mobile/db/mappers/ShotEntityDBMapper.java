package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class ShotEntityDBMapper extends GenericDBMapper {

    @Inject public ShotEntityDBMapper() {
    }

    public ShotEntity fromCursor(Cursor c) {
        ShotEntity shot = new ShotEntity();
        shot.setIdShot(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT)));
        shot.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_USER)));
        shot.setUsername(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USERNAME)));
        shot.setUserPhoto(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USER_PHOTO)));
        shot.setComment(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.COMMENT)));
        shot.setImage(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.IMAGE)));
        shot.setImageWidth(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.IMAGE_WIDTH)));
        shot.setImageHeight(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.IMAGE_HEIGHT)));
        shot.setStreamTitle(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.STREAM_TITLE)));
        shot.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_STREAM)));
        shot.setType(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.TYPE)));
        shot.setNiceCount(c.getInt(c.getColumnIndex(DatabaseContract.ShotTable.NICE_COUNT)));
        shot.setIdShotParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT_PARENT)));
        shot.setIdUserParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_USER_PARENT)));
        shot.setUserNameParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USERNAME_PARENT)));
        shot.setVideoUrl(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_URL)));
        shot.setVideoTitle(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_TITLE)));
        shot.setVideoDuration(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_DURATION)));
        shot.setViews(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.VIEWS)));
        shot.setLinkClicks(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.LINK_CLICKS)));
        shot.setProfileHidden(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.PROFILE_HIDDEN)));
        shot.setReplyCount(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.REPLY_COUNT)));
        shot.setReshootCounter(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.RESHOOT_COUNT)));
        shot.setPromoted(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.PROMOTED)));
        shot.setCtaButtonLink(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.CTA_BUTTON_LINK)));
        shot.setCtaButtonText(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.CTA_BUTTON_TEXT)));
        shot.setCtaCaption(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.CTA_CAPTION)));
        setSynchronizedfromCursor(c, shot);
        return shot;
    }

    public ContentValues toContentValues(ShotEntity shot) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ShotTable.ID_SHOT, shot.getIdShot());
        cv.put(DatabaseContract.ShotTable.ID_USER, shot.getIdUser());
        cv.put(DatabaseContract.ShotTable.USERNAME, shot.getUsername());
        cv.put(DatabaseContract.ShotTable.USER_PHOTO, shot.getUserPhoto());
        cv.put(DatabaseContract.ShotTable.COMMENT, shot.getComment());
        cv.put(DatabaseContract.ShotTable.IMAGE, shot.getImage());
        cv.put(DatabaseContract.ShotTable.IMAGE_WIDTH, shot.getImageWidth());
        cv.put(DatabaseContract.ShotTable.IMAGE_HEIGHT, shot.getImageHeight());
        cv.put(DatabaseContract.ShotTable.STREAM_TITLE, shot.getStreamTitle());
        cv.put(DatabaseContract.ShotTable.ID_STREAM, shot.getIdStream());
        cv.put(DatabaseContract.ShotTable.TYPE, shot.getType());
        cv.put(DatabaseContract.ShotTable.NICE_COUNT, shot.getNiceCount());
        cv.put(DatabaseContract.ShotTable.ID_SHOT_PARENT, shot.getIdShotParent());
        cv.put(DatabaseContract.ShotTable.ID_USER_PARENT, shot.getIdUserParent());
        cv.put(DatabaseContract.ShotTable.USERNAME_PARENT, shot.getUserNameParent());
        cv.put(DatabaseContract.ShotTable.VIDEO_URL, shot.getVideoUrl());
        cv.put(DatabaseContract.ShotTable.VIDEO_TITLE, shot.getVideoTitle());
        cv.put(DatabaseContract.ShotTable.VIDEO_DURATION, shot.getVideoDuration());
        cv.put(DatabaseContract.ShotTable.PROFILE_HIDDEN, shot.getProfileHidden());
        cv.put(DatabaseContract.ShotTable.REPLY_COUNT, shot.getReplyCount());
        cv.put(DatabaseContract.ShotTable.VIEWS, shot.getViews());
        cv.put(DatabaseContract.ShotTable.LINK_CLICKS, shot.getLinkClicks());
        cv.put(DatabaseContract.ShotTable.RESHOOT_COUNT, shot.getReshootCounter());
        cv.put(DatabaseContract.ShotTable.PROMOTED, shot.getPromoted());
        cv.put(DatabaseContract.ShotTable.CTA_BUTTON_LINK, shot.getCtaButtonLink());
        cv.put(DatabaseContract.ShotTable.CTA_BUTTON_TEXT, shot.getCtaButtonText());
        cv.put(DatabaseContract.ShotTable.CTA_CAPTION, shot.getCtaCaption());
        setSynchronizedtoContentValues(shot, cv);
        return cv;
    }
}

