package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.ActivityEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class MeActivityEntityDBMapper extends GenericDBMapper {

  @Inject public MeActivityEntityDBMapper() {
  }

  public ActivityEntity fromCursor(Cursor c) {
    ActivityEntity activity = new ActivityEntity();
    activity.setIdActivity(
        c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.ID_ACTIVITY)));
    activity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.ID_USER)));
    activity.setIdTargetUser(
        c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.ID_TARGET_USER)));
    activity.setUsername(c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.USERNAME)));
    activity.setUserPhoto(
        c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.USER_PHOTO)));
    activity.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.ID_STREAM)));
    activity.setStreamTitle(
        c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.STREAM_TITLE)));
    activity.setComment(c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.COMMENT)));
    activity.setType(c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.TYPE)));
    activity.setIdShot(c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.ID_SHOT)));
    activity.setIdStreamAuthor(
        c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.ID_STREAM_AUTHOR)));
    activity.setIdPoll(c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.ID_POLL)));
    activity.setPollQuestion(
        c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.POLL_QUESTION)));
    setSynchronizedfromCursor(c, activity);
    activity.setPollOptionText(
        c.getString(c.getColumnIndex(DatabaseContract.MeActivityTable.POLL_OPTION_TEXT)));
    activity.setVerified(
        c.getInt(c.getColumnIndex(DatabaseContract.MeActivityTable.IS_VERIFIED)) == 1);
    return activity;
  }

  public ContentValues toContentValues(ActivityEntity activity) {
    ContentValues cv = new ContentValues();
    cv.put(DatabaseContract.MeActivityTable.ID_ACTIVITY, activity.getIdActivity());
    cv.put(DatabaseContract.MeActivityTable.ID_USER, activity.getIdUser());
    cv.put(DatabaseContract.MeActivityTable.ID_TARGET_USER, activity.getIdTargetUser());
    cv.put(DatabaseContract.MeActivityTable.USERNAME, activity.getUsername());
    cv.put(DatabaseContract.MeActivityTable.USER_PHOTO, activity.getUserPhoto());
    cv.put(DatabaseContract.MeActivityTable.ID_STREAM, activity.getIdStream());
    cv.put(DatabaseContract.MeActivityTable.STREAM_TITLE, activity.getStreamTitle());
    cv.put(DatabaseContract.MeActivityTable.COMMENT, activity.getComment());
    cv.put(DatabaseContract.MeActivityTable.TYPE, activity.getType());
    cv.put(DatabaseContract.MeActivityTable.ID_SHOT, activity.getIdShot());
    cv.put(DatabaseContract.MeActivityTable.ID_STREAM_AUTHOR, activity.getIdStreamAuthor());
    cv.put(DatabaseContract.MeActivityTable.POLL_QUESTION, activity.getPollQuestion());
    cv.put(DatabaseContract.MeActivityTable.ID_POLL, activity.getIdPoll());
    cv.put(DatabaseContract.MeActivityTable.POLL_OPTION_TEXT, activity.getPollOptionText());
    cv.put(DatabaseContract.MeActivityTable.IS_VERIFIED, activity.isVerified());
    setSynchronizedtoContentValues(activity, cv);
    return cv;
  }
}
