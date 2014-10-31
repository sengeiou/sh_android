package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import gm.mobi.android.db.DatabaseContract;
import gm.mobi.android.db.objects.ShotEntity;
import java.util.HashMap;
import java.util.Map;

public class ShotMapper extends GenericMapper {

    public ShotEntity fromCursor(Cursor c) {
        ShotEntity shot = new ShotEntity();
        shot.setIdShot(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.ID_SHOT)));
        shot.setIdUser(c.getLong(c.getColumnIndex(DatabaseContract.ShotTable.ID_USER)));
        shot.setComment(c.getString(c.getColumnIndex(DatabaseContract.ShotTable.COMMENT)));
        setSynchronizedfromCursor(c, shot);
        return shot;
    }

    public ContentValues toContentValues(ShotEntity shot) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ShotTable.ID_SHOT, shot.getIdShot());
        cv.put(DatabaseContract.ShotTable.ID_USER, shot.getIdUser());
        cv.put(DatabaseContract.ShotTable.COMMENT, shot.getComment());
        setSynchronizedtoContentValues(shot,cv);
        return cv;
    }

    public ShotEntity fromDto(Map<String, Object> dto) {
        ShotEntity shot = new ShotEntity();
        shot.setIdShot(((Number) dto.get(DatabaseContract.ShotTable.ID_SHOT)).longValue());
        shot.setIdUser(((Number) dto.get(DatabaseContract.ShotTable.ID_USER)).longValue());
        shot.setComment((String) dto.get(DatabaseContract.ShotTable.COMMENT));
        setSynchronizedfromDto(dto, shot);
        return shot;
    }

    public  Map<String, Object> toDto(ShotEntity shot) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(DatabaseContract.ShotTable.ID_SHOT, shot == null ? null : shot.getIdShot());
        dto.put(DatabaseContract.ShotTable.ID_USER, shot == null ? null : shot.getIdUser());
        dto.put(DatabaseContract.ShotTable.COMMENT, shot == null ? null : shot.getComment());
        setSynchronizedtoDto(shot,dto);
        return dto;
    }

}

