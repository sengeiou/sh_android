package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.db.DatabaseContract;

public class FollowEntityDBMapper extends GenericDBMapper {

    public static final String ID_USER = DatabaseContract.FollowTable.ID_USER;
    public static final String ID_FOLLOWED_USER = DatabaseContract.FollowTable.ID_FOLLOWED_USER;
    public static final String IS_FRIEND = DatabaseContract.FollowTable.IS_FRIEND;

    public FollowEntity fromCursor(Cursor c) {
        FollowEntity follow = new FollowEntity();
        follow.setIdUser(c.getString(c.getColumnIndex(ID_USER)));
        follow.setIdFollowedUser(c.getString(c.getColumnIndex(ID_FOLLOWED_USER)));
        follow.setIsFriend(c.getLong(c.getColumnIndex(IS_FRIEND)));
        setSynchronizedfromCursor(c, follow);
        return follow;
    }

    public ContentValues toContentValues(FollowEntity f) {
        ContentValues cv = new ContentValues();
        cv.put(ID_USER, f.getIdUser());
        cv.put(ID_FOLLOWED_USER, f.getIdFollowedUser());
        cv.put(IS_FRIEND, f.isFriend());
        setSynchronizedtoContentValues(f, cv);
        return cv;
    }
}
