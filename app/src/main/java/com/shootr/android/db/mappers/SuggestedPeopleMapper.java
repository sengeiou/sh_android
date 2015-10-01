package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.db.DatabaseContract;
import java.text.Normalizer;

public class SuggestedPeopleMapper extends GenericMapper {

    public SuggestedPeopleMapper() {
    }

    public SuggestedPeopleEntity fromCursor(Cursor c) {
        SuggestedPeopleEntity suggestedPeopleEntity = suggestedPeopleEntityWithCommonFieldsFromCursor(c);
        int emailTokenIndex = c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.EMAIL);
        if (emailTokenIndex >= 0) {
            suggestedPeopleEntity.setEmail(c.getString(emailTokenIndex));
        }
        setSynchronizedfromCursor(c, suggestedPeopleEntity);
        return suggestedPeopleEntity;
    }

    public ContentValues toContentValues(SuggestedPeopleEntity suggestedPeopleEntity) {
        ContentValues cv = new ContentValues();
        if(suggestedPeopleEntity != null){
            String sessionToken = suggestedPeopleEntity.getSessionToken();
            String email = suggestedPeopleEntity.getEmail();
            if (sessionToken != null) {
                cv.put(DatabaseContract.SuggestedPeopleTable.SESSION_TOKEN, sessionToken);
            }
            if (email != null) {
                cv.put(DatabaseContract.SuggestedPeopleTable.EMAIL, email);
            }
            cv.put(DatabaseContract.SuggestedPeopleTable.ID, suggestedPeopleEntity.getIdUser());
            cv.put(DatabaseContract.SuggestedPeopleTable.USER_NAME, suggestedPeopleEntity.getUserName());
            cv.put(DatabaseContract.SuggestedPeopleTable.NAME, suggestedPeopleEntity.getName());
            cv.put(DatabaseContract.SuggestedPeopleTable.PHOTO, suggestedPeopleEntity.getPhoto());
            cv.put(DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWERS, suggestedPeopleEntity.getNumFollowers());
            cv.put(DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWINGS, suggestedPeopleEntity.getNumFollowings());
            cv.put(DatabaseContract.SuggestedPeopleTable.POINTS, suggestedPeopleEntity.getPoints());
            cv.put(DatabaseContract.SuggestedPeopleTable.RANK, suggestedPeopleEntity.getRank());
            cv.put(DatabaseContract.SuggestedPeopleTable.BIO, suggestedPeopleEntity.getBio());
            cv.put(DatabaseContract.SuggestedPeopleTable.WEBSITE, suggestedPeopleEntity.getWebsite());
            cv.put(DatabaseContract.SuggestedPeopleTable.NAME_NORMALIZED,normalizedText(suggestedPeopleEntity.getName()));
            cv.put(DatabaseContract.SuggestedPeopleTable.EMAIL_NORMALIZED,normalizedText(suggestedPeopleEntity.getEmail()));
            cv.put(DatabaseContract.SuggestedPeopleTable.USER_NAME_NORMALIZED,normalizedText(suggestedPeopleEntity.getUserName()));
            cv.put(DatabaseContract.SuggestedPeopleTable.ID_WATCHING_STREAM, suggestedPeopleEntity.getIdWatchingStream());
            cv.put(DatabaseContract.SuggestedPeopleTable.WATCHING_STREAM_TITLE, suggestedPeopleEntity.getWatchingStreamTitle());
            cv.put(DatabaseContract.SuggestedPeopleTable.JOIN_STREAM_DATE, suggestedPeopleEntity.getJoinStreamDate());
            cv.put(DatabaseContract.SuggestedPeopleTable.RELEVANCE, suggestedPeopleEntity.getRelevance());
            setSynchronizedtoContentValues(suggestedPeopleEntity, cv);
        }
        return cv;
    }

    public SuggestedPeopleEntity suggestedPeopleEntityWithCommonFieldsFromCursor(Cursor c){
        SuggestedPeopleEntity suggestedPeople = new SuggestedPeopleEntity();
        suggestedPeople.setIdUser(c.getString(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.ID)));
        suggestedPeople.setUserName(c.getString(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.USER_NAME)));
        suggestedPeople.setName(c.getString(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.NAME)));
        suggestedPeople.setPhoto(c.getString(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.PHOTO)));
        suggestedPeople.setNumFollowers(c.getLong(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWERS)));
        suggestedPeople.setNumFollowings(c.getLong(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWINGS)));
        suggestedPeople.setPoints(c.getLong(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.POINTS)));
        suggestedPeople.setBio(c.getString(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.BIO)));
        suggestedPeople.setRank(c.getLong(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.RANK)));
        suggestedPeople.setWebsite(c.getString(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.WEBSITE)));
        suggestedPeople.setIdWatchingStream(c.getString(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.ID_WATCHING_STREAM)));
        suggestedPeople.setWatchingStreamTitle(c.getString(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.WATCHING_STREAM_TITLE)));
        suggestedPeople.setJoinStreamDate(c.getLong(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.JOIN_STREAM_DATE)));
        suggestedPeople.setRelevance(c.getLong(c.getColumnIndex(DatabaseContract.SuggestedPeopleTable.RELEVANCE)));
        return suggestedPeople;
    }


    public String normalizedText(String s){
        if(s!=null){
            return Normalizer.normalize(s, Normalizer.Form.NFD)
              .replaceAll("[^\\p{ASCII}]", "");
        }else{
            return null;
        }

    }
}
