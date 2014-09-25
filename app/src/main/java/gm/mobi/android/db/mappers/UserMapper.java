package gm.mobi.android.db.mappers;


import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.objects.User;

public class UserMapper extends GenericMapper {

    public static User fromCursor(Cursor c) {
        User user = new User();
        user.setIdUser(c.getInt(c.getColumnIndex(UserTable.ID)));
        user.setFavouriteTeamId(c.getInt(c.getColumnIndex(UserTable.FAVOURITE_TEAM_ID)));
        user.setSessionToken(c.getString(c.getColumnIndex(UserTable.SESSION_TOKEN)));
        user.setUserName(c.getString(c.getColumnIndex(UserTable.USER_NAME)));
        user.setEmail(c.getString(c.getColumnIndex(UserTable.EMAIL)));
        user.setName(c.getString(c.getColumnIndex(UserTable.NAME)));
        user.setPhoto(c.getString(c.getColumnIndex(UserTable.PHOTO)));

        setSynchronizedFromCursor(c, user);

        return user;
    }

    public static User basicFromCursor(Cursor c) {
        User user = new User();
        user.setIdUser(c.getInt(c.getColumnIndex(UserTable.ID)));
        user.setUserName(c.getString(c.getColumnIndex(UserTable.USER_NAME)));
        user.setName(c.getString(c.getColumnIndex(UserTable.NAME)));
        user.setPhoto(c.getString(c.getColumnIndex(UserTable.PHOTO)));
        return user;
    }

    public static ContentValues toContentValues(User u) {
        ContentValues cv = new ContentValues();
        cv.put(UserTable.ID, u.getIdUser());
        cv.put(UserTable.FAVOURITE_TEAM_ID, u.getFavouriteTeamId());
        cv.put(UserTable.SESSION_TOKEN, u.getSessionToken());
        cv.put(UserTable.USER_NAME, u.getUserName());
        cv.put(UserTable.EMAIL, u.getEmail());
        cv.put(UserTable.NAME, u.getName());
        cv.put(UserTable.PHOTO, u.getPhoto());

        setSynchronizedToContentValues(cv, u);

        return cv;
    }

    public static Map<String, Object> reqRestUsersToDto(User user) {
        Map<String, Object> dto = new HashMap<>();

        dto.put(UserTable.ID, user == null ? null : user.getIdUser());
        dto.put(UserTable.FAVOURITE_TEAM_ID, user == null ? null : user.getFavouriteTeamId());
        dto.put(UserTable.USER_NAME, user == null ? null : user.getUserName());
        dto.put(UserTable.NAME, user == null ? null : user.getName());
        dto.put(UserTable.PHOTO, user == null ? null : user.getPhoto());
        setSynchronizedToDto(dto, user);

        return dto;
    }

    public static User fromDto(Map<String, Object> dto) {
        User user = new User();
        user.setIdUser(dto.containsKey(UserTable.ID) ? (Integer) dto.get(UserTable.ID) : null);
        user.setFavouriteTeamId(dto.containsKey(UserTable.FAVOURITE_TEAM_ID) ? (Integer) dto.get(UserTable.FAVOURITE_TEAM_ID) : null);
        user.setSessionToken(dto.containsKey(UserTable.SESSION_TOKEN) ? (String) dto.get(UserTable.SESSION_TOKEN) : null);
        user.setUserName((String) dto.get(UserTable.USER_NAME));
        user.setEmail(dto.containsKey(UserTable.EMAIL) ? (String) dto.get(UserTable.EMAIL) : null);
        user.setName((String) dto.get(UserTable.NAME));
        user.setPhoto((String) dto.get(UserTable.PHOTO));

        setSynchronizedFromDto(dto, user);

        return user;
    }

    public static Map<String, Object> toDto(User user) {
        Map<String, Object> dto = new HashMap<>();

        dto.put(UserTable.ID, user == null ? null : user.getIdUser());
        dto.put(UserTable.FAVOURITE_TEAM_ID, user == null ? null : user.getFavouriteTeamId());
        dto.put(UserTable.SESSION_TOKEN, user == null ? null : user.getSessionToken());
        dto.put(UserTable.USER_NAME, user == null ? null : user.getUserName());
        dto.put(UserTable.EMAIL, user == null ? null : user.getEmail());
        dto.put(UserTable.NAME, user == null ? null : user.getName());
        dto.put(UserTable.PHOTO, user == null ? null : user.getPhoto());

        setSynchronizedToDto(dto, user);

        return dto;
    }


}
