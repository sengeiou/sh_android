package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.data.mapper.UserAvatarUrlProvider;
import com.shootr.android.db.DatabaseContract;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class ActivityEntityMapper extends GenericMapper {

    private  final UserAvatarUrlProvider avatarProvider;

    @Inject
    public ActivityEntityMapper(UserAvatarUrlProvider avatarProvider) {
        this.avatarProvider = avatarProvider;
    }

    public ActivityEntity fromCursor(Cursor c) {
        ActivityEntity activity = new ActivityEntity();
        activity.setIdActivity(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_ACTIVITY)));
        activity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_USER)));
        activity.setUsername(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.USERNAME)));
        activity.setUserPhoto(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.USER_PHOTO)));
        activity.setIdEvent(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_EVENT)));
        activity.setEventTitle(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.EVENT_TITLE)));
        activity.setEventTag(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.EVENT_TAG)));
        activity.setComment(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.COMMENT)));
        activity.setType(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.TYPE)));
        setSynchronizedfromCursor(c, activity);
        return activity;
    }

    public ContentValues toContentValues(ActivityEntity activity) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ActivityTable.ID_ACTIVITY, activity.getIdActivity());
        cv.put(DatabaseContract.ActivityTable.ID_USER, activity.getIdUser());
        cv.put(DatabaseContract.ActivityTable.USERNAME, activity.getUsername());
        cv.put(DatabaseContract.ActivityTable.USER_PHOTO, activity.getUserPhoto());
        cv.put(DatabaseContract.ActivityTable.ID_EVENT, activity.getIdEvent());
        cv.put(DatabaseContract.ActivityTable.EVENT_TITLE, activity.getEventTitle());
        cv.put(DatabaseContract.ActivityTable.EVENT_TAG, activity.getEventTag());
        cv.put(DatabaseContract.ActivityTable.COMMENT, activity.getComment());
        cv.put(DatabaseContract.ActivityTable.TYPE, activity.getType());
        setSynchronizedtoContentValues(activity,cv);
        return cv;
    }

    public ActivityEntity fromDto(Map<String, Object> dto) {
        ActivityEntity activity = new ActivityEntity();
        activity.setIdActivity((String) dto.get(DatabaseContract.ActivityTable.ID_ACTIVITY));
        activity.setIdUser((String) dto.get(DatabaseContract.ActivityTable.ID_USER));
        activity.setUsername((String) dto.get(DatabaseContract.ActivityTable.USERNAME));
        activity.setUserPhoto((String) dto.get(DatabaseContract.ActivityTable.USER_PHOTO));
        activity.setIdEvent((String) dto.get(DatabaseContract.ActivityTable.ID_EVENT));
        activity.setEventTitle((String) dto.get(DatabaseContract.ActivityTable.EVENT_TITLE));
        activity.setEventTag((String) dto.get(DatabaseContract.ActivityTable.EVENT_TAG));
        activity.setComment((String) dto.get(DatabaseContract.ActivityTable.COMMENT));
        activity.setType((String) dto.get(DatabaseContract.ActivityTable.TYPE));

        setSynchronizedfromDto(dto, activity);
        return activity;
    }

    public Map<String, Object> toDto(ActivityEntity activity) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(DatabaseContract.ActivityTable.ID_ACTIVITY, activity == null ? null : activity.getIdActivity());
        dto.put(DatabaseContract.ActivityTable.ID_USER, activity == null ? null : activity.getIdUser());
        dto.put(DatabaseContract.ActivityTable.USERNAME, activity == null ? null : activity.getUsername());
        dto.put(DatabaseContract.ActivityTable.USER_PHOTO, activity == null ? null : activity.getUserPhoto());
        dto.put(DatabaseContract.ActivityTable.ID_EVENT, activity == null ? null : activity.getIdEvent());
        dto.put(DatabaseContract.ActivityTable.EVENT_TITLE, activity == null ? null : activity.getEventTitle());
        dto.put(DatabaseContract.ActivityTable.EVENT_TAG, activity == null ? null : activity.getEventTag());
        dto.put(DatabaseContract.ActivityTable.COMMENT, activity == null ? null : activity.getComment());
        dto.put(DatabaseContract.ActivityTable.TYPE, activity == null ? null : activity.getType());
        setSynchronizedtoDto(activity,dto);
        return dto;
    }

}
