package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class ActivityEntityDBMapper extends GenericDBMapper {

    @Inject public ActivityEntityDBMapper() {
    }

    public ActivityEntity fromCursor(Cursor c) {
        ActivityEntity activity = new ActivityEntity();
        activity.setIdActivity(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_ACTIVITY)));
        activity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_USER)));
        activity.setIdTargetUser(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_TARGET_USER)));
        activity.setUsername(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.USERNAME)));
        activity.setUserPhoto(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.USER_PHOTO)));
        activity.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_STREAM)));
        activity.setStreamTitle(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.STREAM_TITLE)));
        activity.setComment(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.COMMENT)));
        activity.setType(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.TYPE)));
        activity.setIdShot(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_SHOT)));
        activity.setIdStreamAuthor(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_STREAM_AUTHOR)));
        activity.setIdPoll(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.ID_POLL)));
        activity.setPollQuestion(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.POLL_QUESTION)));
        activity.setPollOptionText(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.POLL_OPTION_TEXT)));
        activity.setVerified(c.getInt(c.getColumnIndex(DatabaseContract.ActivityTable.IS_VERIFIED)) == 1);
        activity.setFollowing(
            c.getInt(c.getColumnIndex(DatabaseContract.ActivityTable.IS_FOLLOWING)) == 1);
        activity.setName(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.NAME)));
        activity.setTargetName(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.TARGET_NAME)));
        activity.setStreamPhoto(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.STREAM_PHOTO)));
        activity.setTargetUserPhoto(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.TARGET_USER_PHOTO)));
        activity.setTargetUsername(c.getString(c.getColumnIndex(DatabaseContract.ActivityTable.TARGET_USERNAME)));
        setSynchronizedfromCursor(c, activity);
        return activity;
    }

    public ContentValues toContentValues(ActivityEntity activity) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ActivityTable.ID_ACTIVITY, activity.getIdActivity());
        cv.put(DatabaseContract.ActivityTable.ID_USER, activity.getIdUser());
        cv.put(DatabaseContract.ActivityTable.ID_TARGET_USER, activity.getIdTargetUser());
        cv.put(DatabaseContract.ActivityTable.USERNAME, activity.getUsername());
        cv.put(DatabaseContract.ActivityTable.USER_PHOTO, activity.getUserPhoto());
        cv.put(DatabaseContract.ActivityTable.ID_STREAM, activity.getIdStream());
        cv.put(DatabaseContract.ActivityTable.STREAM_TITLE, activity.getStreamTitle());
        cv.put(DatabaseContract.ActivityTable.COMMENT, activity.getComment());
        cv.put(DatabaseContract.ActivityTable.TYPE, activity.getType());
        cv.put(DatabaseContract.ActivityTable.ID_SHOT, activity.getIdShot());
        cv.put(DatabaseContract.ActivityTable.ID_STREAM_AUTHOR, activity.getIdStreamAuthor());
        cv.put(DatabaseContract.ActivityTable.POLL_QUESTION, activity.getPollQuestion());
        cv.put(DatabaseContract.ActivityTable.ID_POLL, activity.getIdPoll());
        cv.put(DatabaseContract.ActivityTable.POLL_OPTION_TEXT, activity.getPollOptionText());
        cv.put(DatabaseContract.ActivityTable.IS_VERIFIED, activity.isVerified());
        cv.put(DatabaseContract.ActivityTable.IS_FOLLOWING, activity.isFollowing());
        cv.put(DatabaseContract.ActivityTable.NAME, activity.getName());
        cv.put(DatabaseContract.ActivityTable.TARGET_NAME, activity.getTargetName());
        cv.put(DatabaseContract.ActivityTable.STREAM_PHOTO, activity.getStreamPhoto());
        cv.put(DatabaseContract.ActivityTable.TARGET_USER_PHOTO, activity.getTargetUserPhoto());
        cv.put(DatabaseContract.ActivityTable.TARGET_USERNAME, activity.getTargetUsername());
        setSynchronizedtoContentValues(activity, cv);
        return cv;
    }
}
