package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.PollOptionEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class PollOptionEntityDBMapper extends GenericDBMapper {

  @Inject public PollOptionEntityDBMapper() {
  }

  public PollOptionEntity fromCursor(Cursor c) {
    PollOptionEntity pollOptionEntity = new PollOptionEntity();
    pollOptionEntity.setText(c.getString(c.getColumnIndex(DatabaseContract.PollOptionTable.TEXT)));
    pollOptionEntity.setIdPoll(
        c.getString(c.getColumnIndex(DatabaseContract.PollOptionTable.ID_POLL)));
    pollOptionEntity.setIdPollOption(
        c.getString(c.getColumnIndex(DatabaseContract.PollOptionTable.ID_POLL_OPTION)));
    pollOptionEntity.setImageUrl(
        c.getString(c.getColumnIndex(DatabaseContract.PollOptionTable.IMAGE_URL)));
    pollOptionEntity.setVotes(c.getLong(c.getColumnIndex(DatabaseContract.PollOptionTable.VOTES)));
    pollOptionEntity.setOrder(c.getInt(c.getColumnIndex(DatabaseContract.PollOptionTable.ORDER)));
    return pollOptionEntity;
  }

  public ContentValues toContentValues(PollOptionEntity pollOptionEntity) {
    ContentValues cv = new ContentValues();
    cv.put(DatabaseContract.PollOptionTable.ID_POLL, pollOptionEntity.getIdPoll());
    cv.put(DatabaseContract.PollOptionTable.ID_POLL_OPTION, pollOptionEntity.getIdPollOption());
    cv.put(DatabaseContract.PollOptionTable.IMAGE_URL, pollOptionEntity.getImageUrl());
    cv.put(DatabaseContract.PollOptionTable.TEXT, pollOptionEntity.getText());
    cv.put(DatabaseContract.PollOptionTable.VOTES, pollOptionEntity.getVotes());
    cv.put(DatabaseContract.PollOptionTable.ORDER, pollOptionEntity.getOrder());
    return cv;
  }
}
