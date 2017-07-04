package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.db.DatabaseContract.UserTable;
import java.text.Normalizer;

public class UserEntityDBMapper extends GenericDBMapper {

  public UserEntityDBMapper() {
  }

  public UserEntity fromCursor(Cursor c) {
    UserEntity user = new UserEntity();
    fillEntityWithCursor(user, c);
    return user;
  }

  public ContentValues toContentValues(UserEntity u) {
    if (u != null) {
      ContentValues cv = new ContentValues();
      fillContentValuesWithEntity(cv, u);
      return cv;
    } else {
      return null;
    }
  }

  protected void fillContentValuesWithEntity(ContentValues contentValues, UserEntity entity) {
    contentValues.put(UserTable.ID, entity.getIdUser());
    contentValues.put(UserTable.USER_NAME, entity.getUserName());
    contentValues.put(UserTable.EMAIL, entity.getEmail());
    contentValues.put(UserTable.NAME, entity.getName());
    contentValues.put(UserTable.PHOTO, entity.getPhoto());
    contentValues.put(UserTable.NUM_FOLLOWERS, entity.getNumFollowers());
    contentValues.put(UserTable.NUM_FOLLOWINGS, entity.getNumFollowings());
    contentValues.put(UserTable.POINTS, entity.getPoints());
    contentValues.put(UserTable.RANK, entity.getRank());
    contentValues.put(UserTable.BIO, entity.getBio());
    contentValues.put(UserTable.WEBSITE, entity.getWebsite());
    contentValues.put(UserTable.NAME_NORMALIZED, normalizedText(entity.getName()));
    contentValues.put(UserTable.EMAIL_NORMALIZED, normalizedText(entity.getEmail()));
    contentValues.put(UserTable.EMAIL_CONFIRMED, entity.getEmailConfirmed());
    contentValues.put(UserTable.USER_VERIFIED, entity.getVerifiedUser());
    contentValues.put(UserTable.USER_NAME_NORMALIZED, normalizedText(entity.getUserName()));
    contentValues.put(UserTable.ID_WATCHING_STREAM, entity.getIdWatchingStream());
    contentValues.put(UserTable.WATCHING_STREAM_TITLE, entity.getWatchingStreamTitle());
    contentValues.put(UserTable.JOIN_STREAM_DATE, entity.getJoinStreamDate());
    contentValues.put(UserTable.WATCHING_SYNCHRONIZED, entity.getWatchSynchronizedStatus());
    contentValues.put(UserTable.CREATED_STREAMS_COUNT, entity.getCreatedStreamsCount());
    contentValues.put(UserTable.FAVORITED_STREAMS_COUNT, entity.getFavoritedStreamsCount());
    contentValues.put(UserTable.SOCIAL_LOGIN,
        (entity.getSocialLogin() == null ? false : entity.getSocialLogin()));
    contentValues.put(UserTable.RECEIVED_REACTIONS, entity.getReceivedReactions());
    contentValues.put(UserTable.ANALYTICS_USER_TYPE, entity.getAnalyticsUserType());
    contentValues.put(UserTable.NUM_MUTUALS, entity.getNumMutuals());
    contentValues.put(UserTable.FIRST_SESSION_ACTIVATION,
        (entity.isFirstSessionActivation() == null ? false : entity.isFirstSessionActivation()));
    contentValues.put(UserTable.STRATEGIC, entity.getStrategic());
    setSynchronizedtoContentValues(entity, contentValues);
  }

  protected void fillEntityWithCursor(UserEntity entity, Cursor cursor) {
    entity.setIdUser(cursor.getString(cursor.getColumnIndex(UserTable.ID)));
    entity.setUserName(cursor.getString(cursor.getColumnIndex(UserTable.USER_NAME)));
    entity.setEmail(cursor.getString(cursor.getColumnIndex(UserTable.EMAIL)));
    entity.setName(cursor.getString(cursor.getColumnIndex(UserTable.NAME)));
    entity.setEmail(cursor.getString(cursor.getColumnIndex(UserTable.EMAIL)));
    entity.setEmailConfirmed(cursor.getInt(cursor.getColumnIndex(UserTable.EMAIL_CONFIRMED)));
    entity.setVerifiedUser(cursor.getInt(cursor.getColumnIndex(UserTable.USER_VERIFIED)));
    entity.setPhoto(cursor.getString(cursor.getColumnIndex(UserTable.PHOTO)));
    entity.setNumFollowers(cursor.getLong(cursor.getColumnIndex(UserTable.NUM_FOLLOWERS)));
    entity.setNumFollowings(cursor.getLong(cursor.getColumnIndex(UserTable.NUM_FOLLOWINGS)));
    entity.setPoints(cursor.getLong(cursor.getColumnIndex(UserTable.POINTS)));
    entity.setBio(cursor.getString(cursor.getColumnIndex(UserTable.BIO)));
    entity.setRank(cursor.getLong(cursor.getColumnIndex(UserTable.RANK)));
    entity.setWebsite(cursor.getString(cursor.getColumnIndex(UserTable.WEBSITE)));
    entity.setIdWatchingStream(
        cursor.getString(cursor.getColumnIndex(UserTable.ID_WATCHING_STREAM)));
    entity.setWatchingStreamTitle(
        cursor.getString(cursor.getColumnIndex(UserTable.WATCHING_STREAM_TITLE)));
    entity.setJoinStreamDate(cursor.getLong(cursor.getColumnIndex(UserTable.JOIN_STREAM_DATE)));
    entity.setWatchSynchronizedStatus(
        cursor.getString(cursor.getColumnIndex(UserTable.WATCHING_SYNCHRONIZED)));
    entity.setCreatedStreamsCount(
        cursor.getLong(cursor.getColumnIndex(UserTable.CREATED_STREAMS_COUNT)));
    entity.setFavoritedStreamsCount(
        cursor.getLong(cursor.getColumnIndex(UserTable.FAVORITED_STREAMS_COUNT)));
    entity.setSocialLogin(
        (cursor.getInt(cursor.getColumnIndex(UserTable.SOCIAL_LOGIN)) == 1) ? true : false);
    entity.setAnalyticsUserType(
        cursor.getString(cursor.getColumnIndex(UserTable.ANALYTICS_USER_TYPE)));
    entity.setReceivedReactions(
        cursor.getLong(cursor.getColumnIndex(UserTable.RECEIVED_REACTIONS)));
    entity.setNumMutuals(cursor.getLong(cursor.getColumnIndex(UserTable.NUM_MUTUALS)));
    entity.setFirstSessionActivation(
        (cursor.getInt(cursor.getColumnIndex(UserTable.FIRST_SESSION_ACTIVATION)) == 1) ? true
            : false);
    entity.setStrategic(cursor.getInt(cursor.getColumnIndex(UserTable.STRATEGIC)));
    setSynchronizedfromCursor(cursor, entity);
  }

  public String normalizedText(String s) {
    if (s != null) {
      return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    } else {
      return null;
    }
  }
}
