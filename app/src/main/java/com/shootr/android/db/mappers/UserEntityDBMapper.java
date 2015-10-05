package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.DatabaseContract.UserTable;
import java.text.Normalizer;

public class UserEntityDBMapper extends GenericDBMapper {

    public UserEntityDBMapper() {

    }

    public UserEntity fromCursor(Cursor c) {
        UserEntity user = new UserEntity();
        user.setIdUser(c.getString(c.getColumnIndex(UserTable.ID)));
        user.setUserName(c.getString(c.getColumnIndex(UserTable.USER_NAME)));
        user.setEmail(c.getString(c.getColumnIndex(UserTable.EMAIL)));
        user.setName(c.getString(c.getColumnIndex(UserTable.NAME)));
        user.setEmail(c.getString(c.getColumnIndex(UserTable.EMAIL)));
        user.setEmailConfirmed(c.getInt(c.getColumnIndex(UserTable.EMAIL_CONFIRMED)));
        user.setPhoto(c.getString(c.getColumnIndex(UserTable.PHOTO)));
        user.setNumFollowers(c.getLong(c.getColumnIndex(UserTable.NUM_FOLLOWERS)));
        user.setNumFollowings(c.getLong(c.getColumnIndex(UserTable.NUM_FOLLOWINGS)));
        user.setPoints(c.getLong(c.getColumnIndex(UserTable.POINTS)));
        user.setBio(c.getString(c.getColumnIndex(UserTable.BIO)));
        user.setRank(c.getLong(c.getColumnIndex(UserTable.RANK)));
        user.setWebsite(c.getString(c.getColumnIndex(UserTable.WEBSITE)));
        user.setIdWatchingStream(c.getString(c.getColumnIndex(UserTable.ID_WATCHING_STREAM)));
        user.setWatchingStreamTitle(c.getString(c.getColumnIndex(UserTable.WATCHING_STREAM_TITLE)));
        user.setJoinStreamDate(c.getLong(c.getColumnIndex(UserTable.JOIN_STREAM_DATE)));
        user.setWatchSynchronizedStatus(c.getString(c.getColumnIndex(UserTable.WATCHING_SYNCHRONIZED)));

        setSynchronizedfromCursor(c, user);
        return user;
    }

    public ContentValues toContentValues(UserEntity u) {
        ContentValues cv = new ContentValues();
        cv.put(UserTable.ID, u.getIdUser());
        cv.put(UserTable.USER_NAME, u.getUserName());
        cv.put(UserTable.EMAIL, u.getEmail());
        cv.put(UserTable.NAME, u.getName());
        cv.put(UserTable.PHOTO, u.getPhoto());
        cv.put(UserTable.NUM_FOLLOWERS, u.getNumFollowers());
        cv.put(UserTable.NUM_FOLLOWINGS, u.getNumFollowings());
        cv.put(UserTable.POINTS, u.getPoints());
        cv.put(UserTable.RANK, u.getRank());
        cv.put(UserTable.BIO, u.getBio());
        cv.put(UserTable.WEBSITE, u.getWebsite());
        cv.put(UserTable.NAME_NORMALIZED, normalizedText(u.getName()));
        cv.put(UserTable.EMAIL_NORMALIZED, normalizedText(u.getEmail()));
        cv.put(UserTable.EMAIL_CONFIRMED, u.getEmailConfirmed());
        cv.put(UserTable.USER_NAME_NORMALIZED, normalizedText(u.getUserName()));
        cv.put(UserTable.ID_WATCHING_STREAM, u.getIdWatchingStream());
        cv.put(UserTable.WATCHING_STREAM_TITLE, u.getWatchingStreamTitle());
        cv.put(UserTable.JOIN_STREAM_DATE, u.getJoinStreamDate());
        cv.put(UserTable.WATCHING_SYNCHRONIZED, u.getWatchSynchronizedStatus());
        setSynchronizedtoContentValues(u, cv);
        return cv;
    }

    public String normalizedText(String s) {
        if (s != null) {
            return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        } else {
            return null;
        }
    }
}
