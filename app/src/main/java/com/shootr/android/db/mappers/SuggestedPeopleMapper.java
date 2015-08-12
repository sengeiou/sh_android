package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.db.DatabaseContract;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

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

    public SuggestedPeopleEntity fromDto(Map<String, Object> dto) {
        SuggestedPeopleEntity suggestedPeopleEntity = new SuggestedPeopleEntity();
        suggestedPeopleEntity.setIdUser(dto.containsKey(DatabaseContract.SuggestedPeopleTable.ID)
          ? (String) dto.get(DatabaseContract.SuggestedPeopleTable.ID) : null);
        suggestedPeopleEntity.setSessionToken(dto.containsKey(DatabaseContract.SuggestedPeopleTable.SESSION_TOKEN) ? (String) dto.get(
          DatabaseContract.SuggestedPeopleTable.SESSION_TOKEN) : null);
        suggestedPeopleEntity.setUserName(dto.containsKey(DatabaseContract.SuggestedPeopleTable.USER_NAME) ? (String) dto.get(
          DatabaseContract.SuggestedPeopleTable.USER_NAME) : null);
        suggestedPeopleEntity.setEmail(dto.containsKey(DatabaseContract.SuggestedPeopleTable.EMAIL)
          ? (String) dto.get(DatabaseContract.SuggestedPeopleTable.EMAIL) : null);
        suggestedPeopleEntity.setName(dto.containsKey(DatabaseContract.SuggestedPeopleTable.NAME)
          ? (String) dto.get(DatabaseContract.SuggestedPeopleTable.NAME) : null);
        suggestedPeopleEntity.setPhoto(dto.containsKey(DatabaseContract.SuggestedPeopleTable.PHOTO)
          ? (String) dto.get(DatabaseContract.SuggestedPeopleTable.PHOTO) : null);
        suggestedPeopleEntity.setNumFollowers(dto.containsKey(DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWERS) ? ((Number) dto.get(
          DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWERS)).longValue() : null);
        suggestedPeopleEntity.setNumFollowings(dto.containsKey(DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWINGS) ? ((Number) dto.get(
          DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWINGS)).longValue() : null);
        if(dto.get(DatabaseContract.SuggestedPeopleTable.POINTS)!= null){
            suggestedPeopleEntity.setPoints(dto.containsKey(DatabaseContract.SuggestedPeopleTable.POINTS) ? ((Number) dto.get(
              DatabaseContract.SuggestedPeopleTable.POINTS)).longValue() : null);
        }
        suggestedPeopleEntity.setWebsite(dto.containsKey(DatabaseContract.SuggestedPeopleTable.WEBSITE) ? (String) dto.get(
          DatabaseContract.SuggestedPeopleTable.WEBSITE) : null);
        suggestedPeopleEntity.setBio(dto.containsKey(DatabaseContract.SuggestedPeopleTable.BIO)
          ? (String) dto.get(DatabaseContract.SuggestedPeopleTable.BIO) : null);
        if(dto.get(DatabaseContract.SuggestedPeopleTable.RANK) != null){
            suggestedPeopleEntity.setRank(dto.containsKey(DatabaseContract.SuggestedPeopleTable.RANK) ? ((Number) dto.get(
              DatabaseContract.SuggestedPeopleTable.RANK)).longValue() : null);
        }
        String eventId = (String) dto.get(DatabaseContract.SuggestedPeopleTable.ID_WATCHING_STREAM);
        if (eventId != null) {
            suggestedPeopleEntity.setIdWatchingStream(eventId);
        }

        suggestedPeopleEntity.setWatchingStreamTitle(
          dto.containsKey(DatabaseContract.SuggestedPeopleTable.WATCHING_STREAM_TITLE) ? (String) dto.get(
            DatabaseContract.SuggestedPeopleTable.WATCHING_STREAM_TITLE) : null);
        if(dto.get(DatabaseContract.SuggestedPeopleTable.JOIN_STREAM_DATE)!=null) {
            suggestedPeopleEntity.setJoinStreamDate(
              dto.containsKey(DatabaseContract.SuggestedPeopleTable.JOIN_STREAM_DATE) ? ((Number) dto.get(
                DatabaseContract.SuggestedPeopleTable.JOIN_STREAM_DATE)).longValue() : null);
        }

        suggestedPeopleEntity.setRelevance(dto.containsKey(DatabaseContract.SuggestedPeopleTable.RELEVANCE) ? ((Number) dto
            .get(DatabaseContract.SuggestedPeopleTable.RELEVANCE)).longValue() : null);

        setSynchronizedfromDto(dto,suggestedPeopleEntity);
        return suggestedPeopleEntity;
    }

    //TODO bad smell: nombre de método ofuscado
    public Map<String, Object> reqRestUsersToDto(SuggestedPeopleEntity suggestedPeopleEntity) {
        Map<String, Object> dto = new HashMap<>();
        return fillDtoWithCommonFields(dto, suggestedPeopleEntity);
    }


    public Map<String, Object> toDto(SuggestedPeopleEntity suggestedPeopleEntity) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(DatabaseContract.SuggestedPeopleTable.EMAIL, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getEmail());
        dto = fillDtoWithCommonFields(dto, suggestedPeopleEntity);
        return dto;
    }

    public Map<String, Object> fillDtoWithCommonFields(Map<String, Object> dto, SuggestedPeopleEntity suggestedPeopleEntity){
        dto.put(DatabaseContract.SuggestedPeopleTable.ID, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getIdUser());
        dto.put(DatabaseContract.SuggestedPeopleTable.USER_NAME, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getUserName());
        dto.put(DatabaseContract.SuggestedPeopleTable.NAME, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getName());
        dto.put(DatabaseContract.SuggestedPeopleTable.PHOTO, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getPhoto());
        dto.put(DatabaseContract.SuggestedPeopleTable.POINTS, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getPoints());
        dto.put(DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWERS, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getNumFollowers());
        dto.put(DatabaseContract.SuggestedPeopleTable.NUM_FOLLOWINGS, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getNumFollowings());
        dto.put(DatabaseContract.SuggestedPeopleTable.BIO, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getBio());
        dto.put(DatabaseContract.SuggestedPeopleTable.RANK, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getRank());
        dto.put(DatabaseContract.SuggestedPeopleTable.WEBSITE, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getWebsite());
        dto.put(DatabaseContract.SuggestedPeopleTable.ID_WATCHING_STREAM, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getIdWatchingStream());
        dto.put(DatabaseContract.SuggestedPeopleTable.WATCHING_STREAM_TITLE, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getWatchingStreamTitle());
        dto.put(DatabaseContract.SuggestedPeopleTable.JOIN_STREAM_DATE, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getJoinStreamDate());
        dto.put(DatabaseContract.SuggestedPeopleTable.RELEVANCE, suggestedPeopleEntity == null ? null : suggestedPeopleEntity.getRelevance());
        setSynchronizedtoDto(suggestedPeopleEntity, dto);
        return dto;
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
