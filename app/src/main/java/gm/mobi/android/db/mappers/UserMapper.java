package gm.mobi.android.db.mappers;


import android.content.ContentValues;
import android.database.Cursor;

import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.objects.User;

public class UserMapper extends GenericMapper {

    public static User fromCursor(Cursor c) {
        User user = new User();
        user.setId(c.getInt(c.getColumnIndex(UserTable.ID)));
        user.setFavouriteTeamId(c.getColumnIndex(UserTable.FAVOURITE_TEAM_ID));
        user.setSessionToken(c.getString(c.getColumnIndex(UserTable.SESSION_TOKEN)));
        user.setUserName(c.getString(c.getColumnIndex(UserTable.USER_NAME)));
        user.setEmail(c.getString(c.getColumnIndex(UserTable.EMAIL)));
        user.setName(c.getString(c.getColumnIndex(UserTable.NAME)));
        user.setPhoto(c.getString(c.getColumnIndex(UserTable.PHOTO)));

        setSynchronizedFromCursor(c, user);

        return user;
    }

    public static ContentValues toContentValues(User u) {
        ContentValues cv = new ContentValues();
        cv.put(UserTable.ID, u.getId());
        cv.put(UserTable.FAVOURITE_TEAM_ID, u.getFavouriteTeamId());
        cv.put(UserTable.SESSION_TOKEN, u.getSessionToken());
        cv.put(UserTable.USER_NAME, u.getUserName());
        cv.put(UserTable.EMAIL, u.getEmail());
        cv.put(UserTable.NAME, u.getName());
        cv.put(UserTable.PHOTO, u.getPhoto());

        setSynchronizedToContentValues(cv, u);

        return cv;
    }


}
