package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.domain.model.poll.PollStatus;
import javax.inject.Inject;

public class PollEntityDBMapper extends GenericDBMapper {

  @Inject public PollEntityDBMapper() {
  }

  public PollEntity fromCursor(Cursor c) {
    PollEntity pollEntity = new PollEntity();
    pollEntity.setUserHasVoted(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.HAS_VOTED)) == 1L);
    pollEntity.setIdPoll(c.getString(c.getColumnIndex(DatabaseContract.PollTable.ID_POLL)));
    pollEntity.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.PollTable.ID_STREAM)));
    pollEntity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.PollTable.ID_USER)));
    pollEntity.setPublished(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.PUBLISHED)));
    pollEntity.setStatus(c.getString(c.getColumnIndex(DatabaseContract.PollTable.STATUS)));
    pollEntity.setVoteStatus(c.getString(c.getColumnIndex(DatabaseContract.PollTable.VOTE_STATUS)));
    pollEntity.setQuestion(c.getString(c.getColumnIndex(DatabaseContract.PollTable.QUESTION)));
    pollEntity.setVotePrivacy(c.getString(c.getColumnIndex(DatabaseContract.PollTable.VOTE_PRIVACY)));
    pollEntity.setExpirationDate(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.EXPIRATION_DATE)));
    return pollEntity;
  }

  public ContentValues toContentValues(PollEntity pollEntity) {
    ContentValues cv = new ContentValues();
    cv.put(DatabaseContract.PollTable.HAS_VOTED, pollEntity.getUserHasVoted() ? 1L : 0L);
    cv.put(DatabaseContract.PollTable.ID_POLL, pollEntity.getIdPoll());
    cv.put(DatabaseContract.PollTable.ID_STREAM, pollEntity.getIdStream());
    cv.put(DatabaseContract.PollTable.ID_USER, pollEntity.getIdStream());
    cv.put(DatabaseContract.PollTable.PUBLISHED, pollEntity.getPublished());
    cv.put(DatabaseContract.PollTable.QUESTION, pollEntity.getQuestion());
    cv.put(DatabaseContract.PollTable.STATUS, pollEntity.getStatus());
    cv.put(DatabaseContract.PollTable.VOTE_STATUS,
        pollEntity.getVoteStatus() != null ? pollEntity.getVoteStatus() : PollStatus.VOTE);
    cv.put(DatabaseContract.PollTable.VOTE_PRIVACY, pollEntity.getVotePrivacy());
    cv.put(DatabaseContract.PollTable.EXPIRATION_DATE, pollEntity.getExpirationDate());
    return cv;
  }
}
