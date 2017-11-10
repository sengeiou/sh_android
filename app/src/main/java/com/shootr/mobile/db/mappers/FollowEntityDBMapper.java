package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.db.DatabaseContract;

public class FollowEntityDBMapper {

    private static final String ID_FOLLOWED_USER = DatabaseContract.FollowTable.ID_FOLLOWED_ENTITY;
    private static final String IS_FOLOWING = DatabaseContract.FollowTable.IS_FOLLOWING;

    public FollowEntity fromCursor(Cursor c) {
        FollowEntity follow = new FollowEntity();
        follow.setIdFollowed(c.getString(c.getColumnIndex(ID_FOLLOWED_USER)));
        follow.setFollowing(c.getInt(c.getColumnIndex(IS_FOLOWING)) == 1);
        follow.setType(c.getString(c.getColumnIndex(DatabaseContract.FollowTable.TYPE)));
        return follow;
    }

    public ContentValues toContentValues(FollowEntity followEntity) {
        ContentValues cv = new ContentValues();
        cv.put(ID_FOLLOWED_USER, followEntity.getIdFollowedUser());
        cv.put(IS_FOLOWING, followEntity.isFollowing());
        cv.put(DatabaseContract.FollowTable.TYPE, followEntity.getType());
        return cv;
    }
}
