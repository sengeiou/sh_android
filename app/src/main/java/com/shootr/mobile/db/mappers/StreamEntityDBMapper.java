package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.StreamSearchEntity;
import com.shootr.mobile.db.DatabaseContract;
import com.shootr.mobile.domain.repository.SessionRepository;
import javax.inject.Inject;

public class StreamEntityDBMapper extends GenericDBMapper {

  private final SessionRepository sessionRepository;

  @Inject public StreamEntityDBMapper(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  public ContentValues toContentValues(StreamEntity streamEntity) {
    ContentValues contentValues = new ContentValues();
    fillContentValues(streamEntity, contentValues);
    return contentValues;
  }

  private void fillContentValues(StreamEntity streamEntity, ContentValues contentValues) {
    contentValues.put(DatabaseContract.StreamTable.ID_STREAM, streamEntity.getIdStream());
    contentValues.put(DatabaseContract.StreamTable.ID_USER, streamEntity.getIdUser());
    contentValues.put(DatabaseContract.StreamTable.USERNAME, streamEntity.getUserName());
    contentValues.put(DatabaseContract.StreamTable.TITLE, streamEntity.getTitle());
    contentValues.put(DatabaseContract.StreamTable.PHOTO, streamEntity.getPhoto());
    contentValues.put(DatabaseContract.StreamTable.LANDSCAPE_PHOTO,
        streamEntity.getLandscapePhoto());
    contentValues.put(DatabaseContract.StreamTable.DESCRIPTION, streamEntity.getDescription());
    contentValues.put(DatabaseContract.StreamTable.TOPIC, streamEntity.getTopic());
    contentValues.put(DatabaseContract.StreamTable.COUNTRY, streamEntity.getCountry());
    contentValues.put(DatabaseContract.StreamTable.MEDIA_COUNT,
        streamEntity.getMediaCountByRelatedUsers());
    contentValues.put(DatabaseContract.StreamTable.REMOVED, streamEntity.getRemoved());
    contentValues.put(DatabaseContract.StreamTable.TOTAL_FOLLOWERS,
        streamEntity.getTotalFavorites());
    contentValues.put(DatabaseContract.StreamTable.TOTAL_WATCHERS, streamEntity.getTotalWatchers());
    contentValues.put(DatabaseContract.StreamTable.READ_WRITE_MODE,
        streamEntity.getReadWriteMode());
    contentValues.put(DatabaseContract.StreamTable.VERIFIED_USER, streamEntity.getVerifiedUser());
    contentValues.put(DatabaseContract.StreamTable.CONTRIBUTORS_COUNT,
        streamEntity.getContributorCount());
    if (streamEntity.getIdUserContributors() != null) {
      contentValues.put(DatabaseContract.StreamTable.I_AM_CONTRIBUTOR,
          streamEntity.getIdUserContributors().contains(sessionRepository.getCurrentUserId()) ? 1
              : 0);
    } else {
      contentValues.put(DatabaseContract.StreamTable.I_AM_CONTRIBUTOR, 0);
    }
    contentValues.put(DatabaseContract.StreamTable.STRATEGIC, streamEntity.isStrategic());
    contentValues.put(DatabaseContract.StreamTable.MUTED, streamEntity.isMuted());
    contentValues.put(DatabaseContract.StreamTable.FOLLOWING, streamEntity.isFollowing());
    contentValues.put(DatabaseContract.StreamTable.VIEWS, streamEntity.getViews());
    contentValues.put(DatabaseContract.StreamTable.TOTAL_FOLLOWING_WATCHERS,
        streamEntity.getTotalFollowingWatchers());
    contentValues.put(DatabaseContract.StreamTable.PERMISSIONS, streamEntity.getPermissions());
    contentValues.put(DatabaseContract.StreamTable.LAST_TIME_SHOOTED, streamEntity.getLastTimeShooted());
    contentValues.put(DatabaseContract.StreamTable.SHARE_LINK, streamEntity.getShareLink());
    contentValues.put(DatabaseContract.StreamTable.VIDEO_URL, streamEntity.getVideoUrl());

    setSynchronizedtoContentValues(streamEntity, contentValues);
  }

  public StreamEntity fromCursor(Cursor c) {
    StreamEntity streamEntity = new StreamEntity();
    fillStreamEntity(c, streamEntity);
    return streamEntity;
  }

  private void fillStreamEntity(Cursor c, StreamEntity streamEntity) {
    streamEntity.setIdStream(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.ID_STREAM)));
    streamEntity.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.ID_USER)));
    streamEntity.setUserName(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.USERNAME)));
    streamEntity.setTitle(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.TITLE)));
    streamEntity.setPhoto(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.PHOTO)));
    streamEntity.setLandscapePhoto(
        c.getString(c.getColumnIndex(DatabaseContract.StreamTable.LANDSCAPE_PHOTO)));
    streamEntity.setDescription(
        c.getString(c.getColumnIndex(DatabaseContract.StreamTable.DESCRIPTION)));
    streamEntity.setTopic(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.TOPIC)));
    streamEntity.setCountry(c.getString(c.getColumnIndex(DatabaseContract.StreamTable.COUNTRY)));
    streamEntity.setMediaCountByRelatedUsers(
        c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.MEDIA_COUNT)));
    streamEntity.setRemoved(c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.REMOVED)));
    streamEntity.setTotalFavorites(
        c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.TOTAL_FOLLOWERS)));
    streamEntity.setTotalWatchers(
        c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.TOTAL_WATCHERS)));
    streamEntity.setReadWriteMode(
        c.getString(c.getColumnIndex(DatabaseContract.StreamTable.READ_WRITE_MODE)));
    streamEntity.setVerifiedUser(
        c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.VERIFIED_USER)));
    streamEntity.setContributorCount(
        c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.CONTRIBUTORS_COUNT)));
    streamEntity.setiAmContributor(
        c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.I_AM_CONTRIBUTOR)));
    streamEntity.setStrategic(
        (c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.STRATEGIC)) == 1));
    streamEntity.setMuted(
        (c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.MUTED)) == 1));
    streamEntity.setFollowing(
        (c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.FOLLOWING)) == 1));
    streamEntity.setTotalFollowingWatchers(
        c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.TOTAL_FOLLOWING_WATCHERS)));
    streamEntity.setViews(
        c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.VIEWS)));
    streamEntity.setPermissions(
        c.getInt(c.getColumnIndex(DatabaseContract.StreamTable.PERMISSIONS)));
    streamEntity.setLastTimeShooted(
        c.getLong(c.getColumnIndex(DatabaseContract.StreamTable.LAST_TIME_SHOOTED)));
    streamEntity.setShareLink(
        c.getString(c.getColumnIndex(DatabaseContract.StreamTable.SHARE_LINK)));
    streamEntity.setVideoUrl(
        c.getString(c.getColumnIndex(DatabaseContract.StreamTable.VIDEO_URL)));

    setSynchronizedfromCursor(c, streamEntity);
  }

  public StreamSearchEntity fromSearchCursor(Cursor cursor) {
    StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
    fillStreamEntity(cursor, streamSearchEntity);
    streamSearchEntity.setTotalFollowingWatchers(
        cursor.getInt(cursor.getColumnIndex(DatabaseContract.StreamSearchTable.WATCHERS)));
    return streamSearchEntity;
  }
}
