package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.objects.FollowEntity;
import java.util.HashMap;
import java.util.Map;

public class FollowMapper extends GenericMapper {

    public static final String ID_USER = GMContract.FollowTable.ID_USER;
    public static final String ID_FOLLOWED_USER = GMContract.FollowTable.ID_FOLLOWED_USER;

    public FollowEntity fromCursor(Cursor c) {
        FollowEntity follow = new FollowEntity();
        follow.setIdUser(c.getLong(c.getColumnIndex(ID_USER)));
        follow.setFollowedUser(c.getLong(c.getColumnIndex(ID_FOLLOWED_USER)));
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
        follow.setIdUser(((Number) dto.get(ID_USER)) == null ? null : ((Number) dto.get(ID_USER)).longValue());
        follow.setFollowedUser(((Number) dto.get(ID_FOLLOWED_USER)) == null ? null: ((Number) dto.get(ID_FOLLOWED_USER)).longValue());
        setSynchronizedfromDto(dto,follow);
        return follow;
    }

    public  Map<String, Object> toDto(FollowEntity follow) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(ID_USER, follow == null ? null : follow.getIdUser());
        dto.put(ID_FOLLOWED_USER, follow == null ? null : follow.getFollowedUser());
        setSynchronizedtoDto(follow,dto);
        return dto;
    }

}