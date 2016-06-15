package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.domain.PollStatus;
import javax.inject.Inject;

public class PollEntityDBMapper extends GenericDBMapper {

  @Inject public PollEntityDBMapper() {
  }

  public PollEntity fromCursor(Cursor c) {
    PollEntity pollEntity = new PollEntity();
    pollEntity.setHasVoted(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.HAS_VOTED)));
    pollEntity.setIdPoll(c.getString(c.getColumnIndex(DatabaseContract.PollTable.ID_POLL)));
    pollEntity.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.PollTable.ID_STREAM)));
    pollEntity.setPublished(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.PUBLISHED)));
    pollEntity.setStatus(c.getString(c.getColumnIndex(DatabaseContract.PollTable.STATUS)));
    pollEntity.setVoteStatus(c.getString(c.getColumnIndex(DatabaseContract.PollTable.VOTE_STATUS)));
    pollEntity.setQuestion(c.getString(c.getColumnIndex(DatabaseContract.PollTable.QUESTION)));
    return pollEntity;
  }

  public ContentValues toContentValues(PollEntity pollEntity) {
    ContentValues cv = new ContentValues();
    cv.put(DatabaseContract.PollTable.HAS_VOTED, pollEntity.getHasVoted());
    cv.put(DatabaseContract.PollTable.ID_POLL, pollEntity.getIdPoll());
    cv.put(DatabaseContract.PollTable.ID_STREAM, pollEntity.getIdStream());
    cv.put(DatabaseContract.PollTable.PUBLISHED, pollEntity.getPublished());
    cv.put(DatabaseContract.PollTable.QUESTION, pollEntity.getQuestion());
    cv.put(DatabaseContract.PollTable.STATUS, pollEntity.getStatus());
    cv.put(DatabaseContract.PollTable.VOTE_STATUS,
        pollEntity.getVoteStatus() != null ? pollEntity.getVoteStatus() : PollStatus.VOTE);
    return cv;
  }
}
