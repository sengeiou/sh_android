package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.objects.Follow;

public class FollowMapper extends GenericMapper {

    public static Follow fromCursor(Cursor c) {
        Follow follow = new Follow();
        follow.setIdUser(c.getInt(c.getColumnIndex(GMContract.FollowTable.ID_USER)));
        follow.setFollowedUser(c.getInt(c.getColumnIndex(GMContract.FollowTable.ID_FOLLOWED_USER)));
        setSynchronizedFromCursor(c, follow);
        return follow;
    }

    public static ContentValues toContentValues(Follow follow) {
        ContentValues cv = new ContentValues();
        cv.put(GMContract.FollowTable.ID_USER, follow.getIdUser());
        cv.put(GMContract.FollowTable.ID_FOLLOWED_USER, follow.getFollowedUser());
        setSynchronizedToContentValues(cv, follow);
        return cv;
    }

    public static Follow fromDto(Map<String, Object> dto) {
        Follow follow = new Follow();
        follow.setIdUser((Integer) dto.get(GMContract.FollowTable.ID_USER));
        follow.setFollowedUser((Integer) dto.get(GMContract.FollowTable.ID_FOLLOWED_USER));
        setSynchronizedFromDto(dto, follow);
        return follow;
    }

    public static Map<String, Object> toDto(Follow follow) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(GMContract.FollowTable.ID_USER, follow == null ? null : follow.getIdUser());
        dto.put(GMContract.FollowTable.ID_FOLLOWED_USER, follow == null ? null : follow.getFollowedUser());
        setSynchronizedToDto(dto, follow);
        return dto;
    }
}
