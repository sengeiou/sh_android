package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import com.shootr.android.data.entity.UserCreateAccountEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.DatabaseContract.UserTable;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class UserMapper extends GenericMapper {


    public UserMapper(){

    }

    public UserEntity fromCursor(Cursor c) {
        UserEntity user = userEntityWithCommonFieldsFromCursor(c);
        // Fields that might not come from server for all users
        int sessionTokenIndex = c.getColumnIndex(UserTable.SESSION_TOKEN);
        if (sessionTokenIndex >= 0) {
            user.setSessionToken(c.getString(sessionTokenIndex));
        }
        int emailTokenIndex = c.getColumnIndex(UserTable.EMAIL);
        if (emailTokenIndex >= 0) {
            user.setEmail(c.getString(emailTokenIndex));
        }
        setSynchronizedfromCursor(c,user);
        return user;
    }

    public  ContentValues toContentValues(UserEntity u) {
        ContentValues cv = new ContentValues();
        if(u != null){
            String sessionToken = u.getSessionToken();
            String email = u.getEmail();
            if (sessionToken != null) {
                cv.put(UserTable.SESSION_TOKEN, sessionToken);
            }
            if (email != null) {
                cv.put(UserTable.EMAIL, email);
            }
            cv.put(UserTable.ID, u.getIdUser());
            cv.put(UserTable.USER_NAME, u.getUserName());
            cv.put(UserTable.NAME, u.getName());
            cv.put(UserTable.PHOTO, u.getPhoto());
            cv.put(UserTable.NUM_FOLLOWERS, u.getNumFollowers());
            cv.put(UserTable.NUM_FOLLOWINGS, u.getNumFollowings());
            cv.put(UserTable.POINTS, u.getPoints());
            cv.put(UserTable.RANK, u.getRank());
            cv.put(UserTable.BIO, u.getBio());
            cv.put(UserTable.WEBSITE, u.getWebsite());
            cv.put(UserTable.NAME_NORMALIZED,normalizedText(u.getName()));
            cv.put(UserTable.EMAIL_NORMALIZED,normalizedText(u.getEmail()));
            cv.put(UserTable.USER_NAME_NORMALIZED,normalizedText(u.getUserName()));
            cv.put(UserTable.EVENT_ID, u.getIdEvent());
            cv.put(UserTable.EVENT_TITLE, u.getEventTitle());
            cv.put(UserTable.STATUS, u.getStatus());
            cv.put(UserTable.CHECK_IN, u.getCheckIn());
            setSynchronizedtoContentValues(u, cv);
        }
        return cv;
    }

    public UserEntity fromDto(Map<String, Object> dto) {
        UserEntity user = new UserEntity();
        user.setIdUser(dto.containsKey(UserTable.ID) ?  (String.valueOf(dto.get(UserTable.ID))) : null);
        user.setSessionToken(
          dto.containsKey(UserTable.SESSION_TOKEN) ? (String.valueOf(dto.get(UserTable.SESSION_TOKEN))) : null);
        user.setUserName(dto.containsKey(UserTable.USER_NAME) ? (String.valueOf(dto.get(UserTable.USER_NAME))) : null);
        user.setEmail(dto.containsKey(UserTable.EMAIL) ? (String.valueOf(dto.get(UserTable.EMAIL))) : null);
        user.setName(dto.containsKey(UserTable.NAME) ? (String.valueOf(dto.get(UserTable.NAME))) : null);
        user.setPhoto(dto.containsKey(UserTable.PHOTO) ? (String.valueOf(dto.get(UserTable.PHOTO))) : null);
        user.setNumFollowers(
          dto.containsKey(UserTable.NUM_FOLLOWERS) ? ((Number) dto.get(UserTable.NUM_FOLLOWERS)).longValue() : null);
        user.setNumFollowings(
          dto.containsKey(UserTable.NUM_FOLLOWINGS) ? ((Number) dto.get(UserTable.NUM_FOLLOWINGS)).longValue() : null);
        user.setPoints(dto.containsKey(UserTable.POINTS) ? ((Number) dto.get(UserTable.POINTS)).longValue() : null);
        user.setWebsite(dto.containsKey(UserTable.WEBSITE) ? (String.valueOf(dto.get(UserTable.WEBSITE))) : null);
        user.setBio(dto.containsKey(UserTable.BIO) ? (String.valueOf(dto.get(UserTable.BIO))) : null);
        user.setRank(dto.containsKey(UserTable.RANK) ? ((Number) dto.get(UserTable.RANK)).longValue() : null);

        String eventId = String.valueOf(dto.get(UserTable.EVENT_ID));
        if (eventId != null) {
            user.setIdEvent(eventId);
        }

        user.setEventTitle(dto.containsKey(UserTable.EVENT_TITLE) ? (String.valueOf(dto.get(UserTable.EVENT_TITLE))) : null);
        user.setStatus(dto.containsKey(UserTable.STATUS) ? ((String.valueOf(dto.get(UserTable.STATUS)))) : null);
        user.setCheckIn(dto.containsKey(UserTable.CHECK_IN) ? ((Number) dto.get(UserTable.CHECK_IN)).intValue() : null);
        setSynchronizedfromDto(dto,user);
        return user;
    }

    //TODO bad smell: nombre de método ofuscado
    public Map<String, Object> reqRestUsersToDto(UserEntity user) {
        Map<String, Object> dto = new HashMap<>();
        return fillDtoWithCommonFields(dto, user);
    }


    public Map<String, Object> toDto(UserEntity user) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(UserTable.EMAIL, user == null ? null : user.getEmail());
        dto.put(UserTable.SESSION_TOKEN, user == null ? null : user.getSessionToken());
        dto = fillDtoWithCommonFields(dto, user);
        return dto;
    }

    public Map<String, Object> fillDtoWithCommonFields(Map<String, Object> dto, UserEntity user){
        dto.put(UserTable.ID, user == null ? null : user.getIdUser());
        dto.put(UserTable.USER_NAME, user == null ? null : user.getUserName());
        dto.put(UserTable.NAME, user == null ? null : user.getName());
        dto.put(UserTable.PHOTO, user == null ? null : user.getPhoto());
        dto.put(UserTable.POINTS, user == null ? null : user.getPoints());
        dto.put(UserTable.NUM_FOLLOWERS, user == null ? null : user.getNumFollowers());
        dto.put(UserTable.NUM_FOLLOWINGS, user == null ? null : user.getNumFollowings());
        dto.put(UserTable.BIO, user == null ? null : user.getBio());
        dto.put(UserTable.RANK, user == null ? null : user.getRank());
        dto.put(UserTable.WEBSITE, user == null ? null : user.getWebsite());
        dto.put(UserTable.EVENT_ID, user == null ? null : user.getIdEvent());
        dto.put(UserTable.EVENT_TITLE, user == null ? null : user.getEventTitle());
        dto.put(UserTable.STATUS, user == null ? null : user.getStatus());
        dto.put(UserTable.CHECK_IN, user == null ? null : user.getCheckIn());
        setSynchronizedtoDto(user, dto);
        return dto;
    }

    public Map<String, Object> toCreateAccountDto(UserCreateAccountEntity userCreateAccountEntity) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(UserTable.EMAIL, userCreateAccountEntity == null ? null : userCreateAccountEntity.getEmail());
        dto.put(UserTable.SESSION_TOKEN, userCreateAccountEntity == null ? null : userCreateAccountEntity.getSessionToken());
        dto = fillDtoWithCommonFieldsForAccountCreation(dto, userCreateAccountEntity);
        return dto;
    }

    public Map<String, Object> fillDtoWithCommonFieldsForAccountCreation(Map<String, Object> dto, UserCreateAccountEntity userCreateAccountEntity){
        dto.put(UserTable.ID, userCreateAccountEntity == null ? null : userCreateAccountEntity.getIdUser());
        dto.put(UserTable.USER_NAME, userCreateAccountEntity == null ? null : userCreateAccountEntity.getUserName());
        dto.put(UserTable.NAME, userCreateAccountEntity == null ? null : userCreateAccountEntity.getName());
        dto.put(UserTable.SESSION_TOKEN, userCreateAccountEntity == null ? null : userCreateAccountEntity.getSessionToken());
        dto.put(UserTable.EMAIL, userCreateAccountEntity == null ? null : userCreateAccountEntity.getEmail());
        dto.put(UserTable.PASSWORD, userCreateAccountEntity == null ? null : userCreateAccountEntity.getHashedPassword());
        setSynchronizedtoDto(userCreateAccountEntity, dto);
        return dto;
    }


    public UserEntity userEntityWithCommonFieldsFromCursor(Cursor c){
        UserEntity user = new UserEntity();
        user.setIdUser(c.getString(c.getColumnIndex(UserTable.ID)));
        user.setUserName(c.getString(c.getColumnIndex(UserTable.USER_NAME)));
        user.setName(c.getString(c.getColumnIndex(UserTable.NAME)));
        user.setPhoto(c.getString(c.getColumnIndex(UserTable.PHOTO)));
        user.setNumFollowers(c.getLong(c.getColumnIndex(UserTable.NUM_FOLLOWERS)));
        user.setNumFollowings(c.getLong(c.getColumnIndex(UserTable.NUM_FOLLOWINGS)));
        user.setPoints(c.getLong(c.getColumnIndex(UserTable.POINTS)));
        user.setBio(c.getString(c.getColumnIndex(UserTable.BIO)));
        user.setRank(c.getLong(c.getColumnIndex(UserTable.RANK)));
        user.setWebsite(c.getString(c.getColumnIndex(UserTable.WEBSITE)));
        user.setIdEvent(c.getString(c.getColumnIndex(UserTable.EVENT_ID)));
        user.setEventTitle(c.getString(c.getColumnIndex(UserTable.EVENT_TITLE)));
        user.setStatus(c.getString(c.getColumnIndex(UserTable.STATUS)));
        user.setCheckIn(c.getInt(c.getColumnIndex(UserTable.CHECK_IN)));
        return user;
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
