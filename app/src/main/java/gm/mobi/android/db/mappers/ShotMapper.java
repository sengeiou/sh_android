package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.objects.Shot;

public class ShotMapper extends GenericMapper {

    public  Shot fromCursor(Cursor c) {
        Shot shot = new Shot();
        shot.setIdShot(c.getLong(c.getColumnIndex(GMContract.ShotTable.ID_SHOT)));
        shot.setIdUser(c.getLong(c.getColumnIndex(GMContract.ShotTable.ID_USER)));
        shot.setComment(c.getString(c.getColumnIndex(GMContract.ShotTable.COMMENT)));
        setSynchronizedfromCursor(c, shot);
        return shot;
    }

    public ContentValues toContentValues(Shot shot) {
        ContentValues cv = new ContentValues();
        cv.put(GMContract.ShotTable.ID_SHOT, shot.getIdShot());
        cv.put(GMContract.ShotTable.ID_USER, shot.getIdUser());
        cv.put(GMContract.ShotTable.COMMENT, shot.getComment());
        setSynchronizedtoContentValues(shot,cv);
        return cv;
    }

    public  Shot fromDto(Map<String, Object> dto) {
        Shot shot = new Shot();
        shot.setIdShot(((Number) dto.get(GMContract.ShotTable.ID_SHOT)).longValue());
        shot.setIdUser(((Number) dto.get(GMContract.ShotTable.ID_USER)).longValue());
        shot.setComment((String) dto.get(GMContract.ShotTable.COMMENT));
        setSynchronizedfromDto(dto, shot);
        return shot;
    }

    public  Map<String, Object> toDto(Shot shot) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(GMContract.ShotTable.ID_SHOT, shot == null ? null : shot.getIdShot());
        dto.put(GMContract.ShotTable.ID_USER, shot == null ? null : shot.getIdUser());
        dto.put(GMContract.ShotTable.COMMENT, shot == null ? null : shot.getComment());
        setSynchronizedtoDto(shot,dto);
        return dto;
    }

}

