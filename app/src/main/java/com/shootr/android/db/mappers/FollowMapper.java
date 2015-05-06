package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import com.shootr.android.db.DatabaseContract;
import com.shootr.android.data.entity.FollowEntity;
import java.util.HashMap;
import java.util.Map;

public class FollowMapper extends GenericMapper {

    public static final String ID_USER = DatabaseContract.FollowTable.ID_USER;
    public static final String ID_FOLLOWED_USER = DatabaseContract.FollowTable.ID_FOLLOWED_USER;

    public FollowEntity fromCursor(Cursor c) {
        FollowEntity follow = new FollowEntity();
        follow.setIdUser(c.getString(c.getColumnIndex(ID_USER)));
        follow.setFollowedUser(c.getString(c.getColumnIndex(ID_FOLLOWED_USER)));
        setSynchronizedfromCursor(c,follow);
        return follow;
    }

    public ContentValues toContentValues(FollowEntity f) {
        ContentValues cv = new ContentValues();
        cv.put(ID_USER, f.getIdUser());
        cv.put(ID_FOLLOWED_USER, f.getFollowedUser());
        setSynchronizedtoContentValues(f,cv);
        return cv;
    }


    public FollowEntity fromDto(Map<String, Object> dto) {
        FollowEntity follow = new FollowEntity();
        follow.setIdUser((String) dto.get(ID_USER));
        follow.setFollowedUser((dto.get(ID_FOLLOWED_USER)) == null ? null: (String)dto.get(ID_FOLLOWED_USER));
        setSynchronizedfromDto(dto,follow);
        return follow;
    }

    public Map<String, Object> toDto(FollowEntity follow) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(ID_USER, follow == null ? null : follow.getIdUser());
        dto.put(ID_FOLLOWED_USER, follow == null ? null : follow.getFollowedUser());
        setSynchronizedtoDto(follow,dto);
        return dto;
    }

}
