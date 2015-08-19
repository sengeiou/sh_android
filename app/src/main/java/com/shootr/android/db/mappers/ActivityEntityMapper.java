package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.ActivityEntity;
import com.shootr.android.db.DatabaseContract;
import javax.inject.Inject;

public class ActivityEntityMapper extends GenericMapper {

    @Inject
    public ActivityEntityMapper() {
    }

    public ActivityEntity fromCursor(Cursor c) {
        ActivityEntity activity = new ActivityEntity();
        activity.setIdActivity(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_ACTIVITY)));
        activity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_USER)));
        activity.setUsername(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.USERNAME)));
        activity.setUserPhoto(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.USER_PHOTO)));
        activity.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_STREAM)));
        activity.setStreamTitle(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.STREAM_TITLE)));
        activity.setStreamTag(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.STREAM_TAG)));
        activity.setComment(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.COMMENT)));
        activity.setType(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.TYPE)));
        activity.setIdShot(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_SHOT)));
        setSynchronizedfromCursor(c, activity);
        return activity;
    }

    public ContentValues toContentValues(ActivityEntity activity) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ActivityTable.ID_ACTIVITY, activity.getIdActivity());
        cv.put(DatabaseContract.ActivityTable.ID_USER, activity.getIdUser());
        cv.put(DatabaseContract.ActivityTable.USERNAME, activity.getUsername());
        cv.put(DatabaseContract.ActivityTable.USER_PHOTO, activity.getUserPhoto());
        cv.put(DatabaseContract.ActivityTable.ID_STREAM, activity.getIdStream());
        cv.put(DatabaseContract.ActivityTable.STREAM_TITLE, activity.getStreamTitle());
        cv.put(DatabaseContract.ActivityTable.STREAM_TAG, activity.getStreamTag());
        cv.put(DatabaseContract.ActivityTable.COMMENT, activity.getComment());
        cv.put(DatabaseContract.ActivityTable.TYPE, activity.getType());
        cv.put(DatabaseContract.ActivityTable.ID_SHOT, activity.getIdShot());
        setSynchronizedtoContentValues(activity,cv);
        return cv;
    }
}
