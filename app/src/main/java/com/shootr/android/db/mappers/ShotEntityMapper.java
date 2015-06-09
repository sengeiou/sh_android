package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import com.shootr.android.data.mapper.UserAvatarUrlBuilder;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.data.entity.ShotEntity;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class ShotEntityMapper extends GenericMapper {

    private  final UserAvatarUrlBuilder avatarBuilder;

    @Inject
    public ShotEntityMapper(UserAvatarUrlBuilder avatarBuilder) {
        this.avatarBuilder = avatarBuilder;
    }

    public ShotEntity fromCursor(Cursor c) {
        ShotEntity shot = new ShotEntity();
        shot.setIdShot(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT)));
        shot.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_USER)));
        shot.setUsername(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USERNAME)));
        shot.setUserPhoto(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USER_PHOTO)));
        shot.setComment(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.COMMENT)));
        shot.setImage(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.IMAGE)));
        shot.setEventTag(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.EVENT_TAG)));
        shot.setEventTitle(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.EVENT_TITLE)));
        shot.setIdEvent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_EVENT)));
        shot.setType(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.TYPE)));
        shot.setIdShotParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT_PARENT)));
        shot.setIdUserParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_USER_PARENT)));
        shot.setUserNameParent(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USERNAME_PARENT)));
        shot.setVideoUrl(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_URL)));
        shot.setVideoTitle(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_TITLE)));
        shot.setVideoDuration(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.VIDEO_DURATION)));
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
        cv.put(DatabaseContract.ShotTable.EVENT_TAG, shot.getEventTag());
        cv.put(DatabaseContract.ShotTable.EVENT_TITLE, shot.getEventTitle());
        cv.put(DatabaseContract.ShotTable.ID_EVENT, shot.getIdEvent());
        cv.put(DatabaseContract.ShotTable.TYPE, shot.getType());
        cv.put(DatabaseContract.ShotTable.ID_SHOT_PARENT, shot.getIdShotParent());
        cv.put(DatabaseContract.ShotTable.ID_USER_PARENT, shot.getIdUserParent());
        cv.put(DatabaseContract.ShotTable.USERNAME_PARENT, shot.getUserNameParent());
        cv.put(DatabaseContract.ShotTable.VIDEO_URL, shot.getVideoUrl());
        cv.put(DatabaseContract.ShotTable.VIDEO_TITLE, shot.getVideoTitle());
        cv.put(DatabaseContract.ShotTable.VIDEO_DURATION, shot.getVideoDuration());
        setSynchronizedtoContentValues(shot,cv);
        return cv;
    }

    public ShotEntity fromDto(Map<String, Object> dto) {
        ShotEntity shot = new ShotEntity();
        shot.setIdShot((String) dto.get(DatabaseContract.ShotTable.ID_SHOT));
        shot.setIdUser((String) dto.get(DatabaseContract.ShotTable.ID_USER));
        shot.setUsername(((String) dto.get(DatabaseContract.ShotTable.USERNAME)));
        // no user photo from dataservice
        shot.setUserPhoto(avatarBuilder.thumbnail(shot.getIdUser()));
        shot.setComment((String) dto.get(DatabaseContract.ShotTable.COMMENT));
        shot.setImage((String) dto.get(DatabaseContract.ShotTable.IMAGE));
        shot.setEventTag((String) dto.get(DatabaseContract.ShotTable.EVENT_TAG));
        shot.setEventTitle((String) dto.get(DatabaseContract.ShotTable.EVENT_TITLE));
        shot.setIdEvent((String) dto.get(DatabaseContract.ShotTable.ID_EVENT));
        shot.setType((String) dto.get(DatabaseContract.ShotTable.TYPE));
        shot.setIdShotParent((String) dto.get(DatabaseContract.ShotTable.ID_SHOT_PARENT));
        shot.setIdUserParent((String) dto.get(DatabaseContract.ShotTable.ID_USER_PARENT));
        shot.setUserNameParent((String) dto.get(DatabaseContract.ShotTable.USERNAME_PARENT));

        shot.setVideoUrl((String) dto.get(DatabaseContract.ShotTable.VIDEO_URL));
        shot.setVideoTitle((String) dto.get(DatabaseContract.ShotTable.VIDEO_TITLE));
        Number videoDuration = (Number) dto.get(DatabaseContract.ShotTable.VIDEO_DURATION);
        if (videoDuration != null) {
            shot.setVideoDuration(videoDuration.longValue());
        }
        setSynchronizedfromDto(dto, shot);
        return shot;
    }

    public Map<String, Object> toDto(ShotEntity shot) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(DatabaseContract.ShotTable.ID_SHOT, shot == null ? null : shot.getIdShot());
        dto.put(DatabaseContract.ShotTable.ID_USER, shot == null ? null : shot.getIdUser());
        dto.put(DatabaseContract.ShotTable.USERNAME, shot == null ? null : shot.getUsername());
        /* no user photo to dataservice */
        dto.put(DatabaseContract.ShotTable.COMMENT, shot == null ? null : shot.getComment());
        dto.put(DatabaseContract.ShotTable.IMAGE, shot == null ? null : shot.getImage());
        dto.put(DatabaseContract.ShotTable.EVENT_TAG, shot == null ? null : shot.getEventTag());
        dto.put(DatabaseContract.ShotTable.EVENT_TITLE, shot == null ? null : shot.getEventTitle());
        dto.put(DatabaseContract.ShotTable.ID_EVENT, shot == null ? null : shot.getIdEvent());
        dto.put(DatabaseContract.ShotTable.TYPE, shot == null ? null : shot.getType());
        dto.put(DatabaseContract.ShotTable.ID_SHOT_PARENT, shot == null ? null : shot.getIdShotParent());
        dto.put(DatabaseContract.ShotTable.ID_USER_PARENT, shot == null ? null : shot.getIdUserParent());
        dto.put(DatabaseContract.ShotTable.USERNAME_PARENT, shot == null ? null : shot.getUserNameParent());
        dto.put(DatabaseContract.ShotTable.VIDEO_URL, shot == null ? null : shot.getVideoUrl());
        dto.put(DatabaseContract.ShotTable.VIDEO_TITLE, shot == null ? null : shot.getVideoTitle());
        dto.put(DatabaseContract.ShotTable.VIDEO_DURATION, shot == null ? null : shot.getVideoDuration());
        setSynchronizedtoDto(shot,dto);
        return dto;
    }

}

