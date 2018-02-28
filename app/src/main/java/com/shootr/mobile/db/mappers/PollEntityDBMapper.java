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
    pollEntity.setIdPoll(c.getString(c.getColumnIndex(DatabaseContract.PollTable.ID_POLL)));
    pollEntity.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.PollTable.ID_STREAM)));
    pollEntity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.PollTable.ID_USER)));
    pollEntity.setPublished(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.PUBLISHED)));
    pollEntity.setStatus(c.getString(c.getColumnIndex(DatabaseContract.PollTable.STATUS)));
    pollEntity.setVoteStatus(c.getString(c.getColumnIndex(DatabaseContract.PollTable.VOTE_STATUS)));
    pollEntity.setQuestion(c.getString(c.getColumnIndex(DatabaseContract.PollTable.QUESTION)));
    pollEntity.setVotePrivacy(c.getString(c.getColumnIndex(DatabaseContract.PollTable.VOTE_PRIVACY)));
    pollEntity.setExpirationDate(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.EXPIRATION_DATE)));
    pollEntity.setHideResults(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.HIDE_RESULTS)) == 1L);
    pollEntity.setVerifiedPoll(
        c.getLong(c.getColumnIndex(DatabaseContract.PollTable.VERIFIED_POLL)) == 1L);
    pollEntity.setCanVote(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.CAN_VOTE)) == 1L);
    pollEntity.setDailyPoll(c.getLong(c.getColumnIndex(DatabaseContract.PollTable.DAILY_POLL)) == 1L);
    pollEntity.setShareLink(
        c.getString(c.getColumnIndex(DatabaseContract.PollTable.SHARE_LINK)));
    return pollEntity;
  }

  public ContentValues toContentValues(PollEntity pollEntity) {
    ContentValues cv = new ContentValues();
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
    cv.put(DatabaseContract.PollTable.VERIFIED_POLL,
        pollEntity.getVerifiedPoll() != null && pollEntity.getVerifiedPoll() ? 1L : 0L);
    cv.put(DatabaseContract.PollTable.HIDE_RESULTS,
        pollEntity.isHideResults() != null && pollEntity.isHideResults() ? 1L : 0L);
    cv.put(DatabaseContract.PollTable.CAN_VOTE,
        pollEntity.canVote() ? 1L : 0L);
    cv.put(DatabaseContract.PollTable.DAILY_POLL,
        pollEntity.isDailyPoll() ? 1L : 0L);
    cv.put(DatabaseContract.PollTable.SHARE_LINK, pollEntity.getShareLink());
    return cv;
  }
}
