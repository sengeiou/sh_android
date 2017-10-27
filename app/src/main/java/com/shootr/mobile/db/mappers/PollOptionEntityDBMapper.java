package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.gson.Gson;
import com.shootr.mobile.data.entity.ImageMediaEntity;
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
    pollOptionEntity.setVotes(c.getLong(c.getColumnIndex(DatabaseContract.PollOptionTable.VOTES)));
    pollOptionEntity.setOrder(c.getInt(c.getColumnIndex(DatabaseContract.PollOptionTable.ORDER)));
    pollOptionEntity.setVoted(c.getInt(c.getColumnIndex(DatabaseContract.PollOptionTable.VOTED)) == 1);
    retrieveEntities(c, pollOptionEntity);
    return pollOptionEntity;
  }

  private void retrieveEntities(Cursor c, PollOptionEntity pollOptionEntity) {
    Gson gson = new Gson();
    String json = c.getString(c.getColumnIndex(DatabaseContract.PollOptionTable.OPTION_IMAGE));
    pollOptionEntity.setOptionImage(gson.fromJson(json, ImageMediaEntity.class));
  }

  public ContentValues toContentValues(PollOptionEntity pollOptionEntity) {
    ContentValues cv = new ContentValues();
    cv.put(DatabaseContract.PollOptionTable.ID_POLL, pollOptionEntity.getIdPoll());
    cv.put(DatabaseContract.PollOptionTable.ID_POLL_OPTION, pollOptionEntity.getIdPollOption());
    cv.put(DatabaseContract.PollOptionTable.TEXT, pollOptionEntity.getText());
    cv.put(DatabaseContract.PollOptionTable.VOTES, pollOptionEntity.getVotes());
    cv.put(DatabaseContract.PollOptionTable.ORDER, pollOptionEntity.getOrder());
    cv.put(DatabaseContract.PollOptionTable.VOTED, pollOptionEntity.isVoted() ? 1 : 0);
    storeImages(pollOptionEntity, cv);
    return cv;
  }

  private void storeImages(PollOptionEntity pollOptionEntity, ContentValues cv) {
    Gson gson = new Gson();
    String json = gson.toJson(pollOptionEntity.getOptionImage());
    cv.put(DatabaseContract.PollOptionTable.OPTION_IMAGE, json);
  }
}
