package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.mapper.UserAvatarUrlProvider;
import com.shootr.android.db.DatabaseContract;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class ShotEntityMapper extends GenericMapper {

    private  final UserAvatarUrlProvider avatarProvider;

    @Inject
    public ShotEntityMapper(UserAvatarUrlProvider avatarProvider) {
        this.avatarProvider = avatarProvider;
    }

    public ShotEntity fromCursor(Cursor c) {
        ShotEntity shot = new ShotEntity();
        shot.setIdShot(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT)));
        shot.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.ID_USER)));
        shot.setUsername(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USERNAME)));
        shot.setUserPhoto(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.USER_PHOTO)));
        shot.setComment(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.COMMENT)));
        shot.setImage(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.IMAGE)));
        shot.setStreamTag(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.STREAM_TAG)));
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
        cv.put(DatabaseContract.ShotTable.STREAM_TAG, shot.getStreamTag());
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
        setSynchronizedtoContentValues(shot,cv);
        return cv;
    }

    public ShotEntity fromDto(Map<String, Object> dto) {
        ShotEntity shot = new ShotEntity();
        shot.setIdShot((String) dto.get(DatabaseContract.ShotTable.ID_SHOT));
        shot.setIdUser((String) dto.get(DatabaseContract.ShotTable.ID_USER));
        shot.setUsername((String) dto.get(DatabaseContract.ShotTable.USERNAME));
        // no user photo from dataservice
        shot.setUserPhoto(avatarProvider.thumbnail(shot.getIdUser()));
        shot.setComment((String) dto.get(DatabaseContract.ShotTable.COMMENT));
        shot.setImage((String) dto.get(DatabaseContract.ShotTable.IMAGE));
        shot.setStreamTag((String) dto.get(DatabaseContract.ShotTable.STREAM_TAG));
        shot.setStreamTitle((String) dto.get(DatabaseContract.ShotTable.STREAM_TITLE));
        shot.setIdStream((String) dto.get(DatabaseContract.ShotTable.ID_STREAM));
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
        dto.put(DatabaseContract.ShotTable.STREAM_TAG, shot == null ? null : shot.getStreamTag());
        dto.put(DatabaseContract.ShotTable.STREAM_TITLE, shot == null ? null : shot.getStreamTitle());
        dto.put(DatabaseContract.ShotTable.ID_STREAM, shot == null ? null : shot.getIdStream());
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

