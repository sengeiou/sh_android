package gm.mobi.android.db.mappers;


import android.content.ContentValues;
import android.database.Cursor;
import gm.mobi.android.db.objects.Synchronized;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.objects.User;

public class UserMapper extends GenericMapper {


    public UserMapper(){}

    public  User fromCursor(Cursor c) {
        User user = getUserByCursor(c);
        // Fields that might not come from server for all users
        int sessionTokenIndex = c.getColumnIndex(UserTable.SESSION_TOKEN);
        if (sessionTokenIndex >= 0) {
            user.setSessionToken(c.getString(sessionTokenIndex));
        }
        int emailTokenIndex = c.getColumnIndex(UserTable.EMAIL);
        if (emailTokenIndex >= 0) {
            user.setEmail(c.getString(emailTokenIndex));
        }
        setSynchronizedfromCursor(c, user);
        return user;
    }

    public  ContentValues toContentValues(User u) {

        ContentValues cv = new ContentValues();
        cv.put(UserTable.ID, u.getIdUser());
        cv.put(UserTable.FAVOURITE_TEAM_ID, u.getFavouriteTeamId());
        cv.put(UserTable.SESSION_TOKEN, u.getSessionToken());
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
        cv.put(UserTable.NAME_NORMALIZED,normalizedText(u.getName()));
        cv.put(UserTable.EMAIL_NORMALIZED,normalizedText(u.getEmail()));
        cv.put(UserTable.USER_NAME_NORMALIZED,normalizedText(u.getUserName()));
        setSynchronizedtoContentValues(u, cv);
        return cv;
    }

    public  ContentValues userToContentValues(User u, User currentUser){
        ContentValues cv = new ContentValues();
        cv.put(UserTable.ID, u.getIdUser());
        cv.put(UserTable.FAVOURITE_TEAM_ID, u.getFavouriteTeamId());
        cv.put(UserTable.SESSION_TOKEN, currentUser.getFavouriteTeamId());
        cv.put(UserTable.USER_NAME, u.getUserName());
        cv.put(UserTable.EMAIL, currentUser.getEmail());
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
        setSynchronizedtoContentValues(u,cv);
        return cv;
    }

    public  Map<String, Object> reqRestUsersToDto(User user) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(UserTable.ID, user == null ? null : user.getIdUser());
        dto.put(UserTable.FAVOURITE_TEAM_ID, user == null ? null : user.getFavouriteTeamId());
        dto.put(UserTable.USER_NAME, user == null ? null : user.getUserName());
        dto.put(UserTable.NAME, user == null ? null : user.getName());
        dto.put(UserTable.PHOTO, user == null ? null : user.getPhoto());
        dto.put(UserTable.POINTS, user == null ? null : user.getPoints());
        dto.put(UserTable.NUM_FOLLOWERS, user == null ? null : user.getNumFollowers());
        dto.put(UserTable.NUM_FOLLOWINGS, user == null ? null : user.getNumFollowings());
        dto.put(UserTable.BIO, user == null ? null : user.getBio());
        dto.put(UserTable.RANK, user == null ? null : user.getRank());
        dto.put(UserTable.WEBSITE, user == null ? null : user.getWebsite());
        setSynchronizedtoDto(user, dto);
        return dto;
    }

    public  User fromDto(Map<String, Object> dto) {
        User user = new User();
        user.setIdUser(dto.containsKey(UserTable.ID) ?  ((Number)dto.get(UserTable.ID)).longValue() : null);
        user.setFavouriteTeamId(dto.containsKey(UserTable.FAVOURITE_TEAM_ID) ? ((Number) dto.get(UserTable.FAVOURITE_TEAM_ID)).longValue() : null);
        user.setSessionToken(dto.containsKey(UserTable.SESSION_TOKEN) ? (String) dto.get(UserTable.SESSION_TOKEN) : null);
        user.setUserName((String) dto.get(UserTable.USER_NAME));
        user.setEmail(dto.containsKey(UserTable.EMAIL) ? (String) dto.get(UserTable.EMAIL) : null);
        user.setName((String) dto.get(UserTable.NAME));
        user.setPhoto((String) dto.get(UserTable.PHOTO));
        user.setNumFollowers(((Number) dto.get(UserTable.NUM_FOLLOWERS)).longValue());
        user.setNumFollowings(((Number) dto.get(UserTable.NUM_FOLLOWINGS)).longValue());
        user.setPoints(((Number) dto.get(UserTable.POINTS)).longValue());
        user.setWebsite((String) dto.get(UserTable.WEBSITE));
        user.setBio((String) dto.get(UserTable.BIO));
        user.setRank(((Number) dto.get(UserTable.RANK)).longValue());
        setSynchronizedfromDto(dto,user);
        return user;
    }



    public  Map<String, Object> toDto(User user) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(UserTable.ID, user == null ? null : user.getIdUser());
        dto.put(UserTable.FAVOURITE_TEAM_ID, user == null ? null : user.getFavouriteTeamId());
        dto.put(UserTable.SESSION_TOKEN, user == null ? null : user.getSessionToken());
        dto.put(UserTable.USER_NAME, user == null ? null : user.getUserName());
        dto.put(UserTable.EMAIL, user == null ? null : user.getEmail());
        dto.put(UserTable.NAME, user == null ? null : user.getName());
        dto.put(UserTable.PHOTO, user == null ? null : user.getPhoto());
        dto.put(UserTable.POINTS, user == null ? null : user.getPoints());
        dto.put(UserTable.NUM_FOLLOWERS, user == null ? null : user.getNumFollowers());
        dto.put(UserTable.NUM_FOLLOWINGS, user == null ? null : user.getNumFollowings());
        dto.put(UserTable.BIO, user == null ? null : user.getBio());
        dto.put(UserTable.RANK, user == null ? null : user.getRank());
        dto.put(UserTable.WEBSITE, user == null ? null : user.getWebsite());
        setSynchronizedtoDto(user,dto);
        return dto;
    }


    public User getUserByCursor(Cursor c){
        User user = new User();
        user.setIdUser(c.getLong(c.getColumnIndex(UserTable.ID)));
        user.setFavouriteTeamId(c.getLong(c.getColumnIndex(UserTable.FAVOURITE_TEAM_ID)));
        user.setUserName(c.getString(c.getColumnIndex(UserTable.USER_NAME)));
        user.setName(c.getString(c.getColumnIndex(UserTable.NAME)));
        user.setPhoto(c.getString(c.getColumnIndex(UserTable.PHOTO)));
        user.setNumFollowers(c.getLong(c.getColumnIndex(UserTable.NUM_FOLLOWERS)));
        user.setNumFollowings(c.getLong(c.getColumnIndex(UserTable.NUM_FOLLOWINGS)));
        user.setPoints(c.getLong(c.getColumnIndex(UserTable.POINTS)));
        user.setBio(c.getString(c.getColumnIndex(UserTable.BIO)));
        user.setRank(c.getLong(c.getColumnIndex(UserTable.RANK)));
        user.setWebsite(c.getString(c.getColumnIndex(UserTable.WEBSITE)));
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
