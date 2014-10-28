package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.objects.UserEntity;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public class UserMapper extends GenericMapper {


    public UserMapper(){}

    public UserEntity fromCursor(Cursor c) {
        UserEntity user = getUserByCursor(c);
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
        String sessionToken = u.getSessionToken();
        String email = u.getEmail();
        cv.put(UserTable.ID, u.getIdUser());
        cv.put(UserTable.FAVORITE_TEAM_ID, u.getFavoriteTeamId());
        cv.put(UserTable.FAVORITE_TEAM_NAME,u.getFavoriteTeamName() );
        cv.put(UserTable.SESSION_TOKEN,sessionToken!=null?sessionToken:null);
        cv.put(UserTable.USER_NAME, u.getUserName());
        cv.put(UserTable.NAME, u.getName());
        cv.put(UserTable.EMAIL, email!=null ? email :null);
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

    //TODO bad smell: nombre de método ofuscado
    public  Map<String, Object> reqRestUsersToDto(UserEntity user) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(UserTable.ID, user == null ? null : user.getIdUser());
        dto.put(UserTable.FAVORITE_TEAM_NAME, user == null ? null : user.getFavoriteTeamName());
        dto.put(UserTable.FAVORITE_TEAM_ID, user == null ? null : user.getFavoriteTeamId());
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

    public UserEntity fromDto(Map<String, Object> dto) {
        UserEntity user = new UserEntity();
        user.setIdUser(dto.containsKey(UserTable.ID) ?  ((Number)dto.get(UserTable.ID)).longValue() : null);
        user.setFavoriteTeamId(dto.containsKey(UserTable.FAVORITE_TEAM_ID) && dto.get(UserTable.FAVORITE_TEAM_ID)!=null ? ((Number) dto.get(UserTable.FAVORITE_TEAM_ID)).longValue(): null);
        user.setFavoriteTeamName(dto.containsKey(UserTable.FAVORITE_TEAM_NAME) ? (String) dto.get(UserTable.FAVORITE_TEAM_NAME) : null);
        user.setSessionToken(dto.containsKey(UserTable.SESSION_TOKEN) ? (String) dto.get(UserTable.SESSION_TOKEN) : null);
        user.setUserName(dto.containsKey(UserTable.USER_NAME) ? ((String) dto.get(UserTable.USER_NAME)) : null);
        user.setEmail(dto.containsKey(UserTable.EMAIL) ? (String) dto.get(UserTable.EMAIL) : null);
        user.setName(dto.containsKey(UserTable.NAME) ? (String) dto.get(UserTable.NAME) : null);
        user.setPhoto(dto.containsKey(UserTable.PHOTO) ? (String) dto.get(UserTable.PHOTO) : null);
        user.setNumFollowers(dto.containsKey(UserTable.NUM_FOLLOWERS) ? ((Number) dto.get(UserTable.NUM_FOLLOWERS)).longValue() : null);
        user.setNumFollowings(dto.containsKey(UserTable.NUM_FOLLOWINGS) ? ((Number) dto.get(UserTable.NUM_FOLLOWINGS)).longValue() : null);
        user.setPoints(dto.containsKey(UserTable.POINTS) ? ((Number) dto.get(UserTable.POINTS)).longValue() : null);
        user.setWebsite(dto.containsKey(UserTable.WEBSITE) ? ((String) dto.get(UserTable.WEBSITE)) : null);
        user.setBio(dto.containsKey(UserTable.BIO) ? ((String) dto.get(UserTable.BIO)) : null);
        user.setRank(dto.containsKey(UserTable.RANK) ? ((Number) dto.get(UserTable.RANK)).longValue() : null);
        setSynchronizedfromDto(dto,user);
        return user;
    }



    public  Map<String, Object> toDto(UserEntity user) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(UserTable.ID, user == null ? null : user.getIdUser());
        dto.put(UserTable.FAVORITE_TEAM_ID, user == null ? null : user.getFavoriteTeamId());
        dto.put(UserTable.FAVORITE_TEAM_NAME, user == null ? null : user.getFavoriteTeamName());
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


    public UserEntity getUserByCursor(Cursor c){
        UserEntity user = new UserEntity();
        user.setIdUser(c.getLong(c.getColumnIndex(UserTable.ID)));
        user.setFavoriteTeamId(c.getLong(c.getColumnIndex(UserTable.FAVORITE_TEAM_ID)));
        user.setFavoriteTeamName(c.getString(c.getColumnIndex(UserTable.FAVORITE_TEAM_NAME)));
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
