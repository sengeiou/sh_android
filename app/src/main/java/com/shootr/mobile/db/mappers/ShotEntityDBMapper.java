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
        shot.setProfileHidden(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.PROFILE_HIDDEN)));
        shot.setReplyCount(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.REPLY_COUNT)));
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
        setSynchronizedtoContentValues(shot, cv);
        return cv;
    }
}

