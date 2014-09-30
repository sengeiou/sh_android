package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.objects.Shot;

public class ShotMapper extends GenericMapper {

    public static Shot fromCursor(Cursor c) {
        Shot shot = new Shot();
        shot.setIdShot(c.getLong(c.getColumnIndex(GMContract.ShotTable.ID_SHOT)));
        shot.setIdUser(c.getLong(c.getColumnIndex(GMContract.ShotTable.ID_USER)));
        shot.setComment(c.getString(c.getColumnIndex(GMContract.ShotTable.COMMENT)));
        setSynchronizedFromCursor(c, shot);
        return shot;
    }

    public static ContentValues toContentValues(Shot shot) {
        ContentValues cv = new ContentValues();
        cv.put(GMContract.ShotTable.ID_SHOT, shot.getIdShot());
        cv.put(GMContract.ShotTable.ID_USER, shot.getIdUser());
        cv.put(GMContract.ShotTable.COMMENT, shot.getComment());
        setSynchronizedToContentValues(cv, shot);
        return cv;
    }

    public static Shot fromDto(Map<String, Object> dto) {
        Shot shot = new Shot();
        shot.setIdShot(((Number) dto.get(GMContract.ShotTable.ID_SHOT)).longValue());
        shot.setIdUser(((Number) dto.get(GMContract.ShotTable.ID_USER)).longValue());
        shot.setComment((String) dto.get(GMContract.ShotTable.COMMENT));
        setSynchronizedFromDto(dto, shot);
        return shot;
    }

    public static Map<String, Object> toDto(Shot shot) {
        Map<String, Object> dto = new HashMap<>();

        dto.put(GMContract.ShotTable.ID_SHOT, shot == null ? null : shot.getIdShot());
        dto.put(GMContract.ShotTable.ID_USER, shot == null ? null : shot.getIdUser());
        dto.put(GMContract.ShotTable.COMMENT, shot == null ? null : shot.getComment());
        setSynchronizedToDto(dto, shot);

        return dto;
    }

}

