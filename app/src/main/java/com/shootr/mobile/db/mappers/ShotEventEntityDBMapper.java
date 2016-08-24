package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.data.entity.ShotEventEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class ShotEventEntityDBMapper extends GenericDBMapper {

    public static final String ID_SHOT = DatabaseContract.ShotEventTable.ID_SHOT;
    public static final String TIMESTAMP = DatabaseContract.ShotEventTable.TIMESTAMP;
    public static final String TYPE = DatabaseContract.ShotEventTable.TYPE;

    @Inject public ShotEventEntityDBMapper() {
    }

    public ShotEventEntity fromCursor(Cursor c) {
        ShotEventEntity shotEventEntity = new ShotEventEntity();
        shotEventEntity.setIdShot(c.getString(c.getColumnIndex(ID_SHOT)));
        shotEventEntity.setType(c.getString(c.getColumnIndex(TYPE)));
        shotEventEntity.setTimestamp(c.getLong(c.getColumnIndex(TIMESTAMP)));
        return shotEventEntity;
    }

    public ContentValues toContentValues(ShotEventEntity shotEventEntity) {
        ContentValues cv = new ContentValues();
        cv.put(ID_SHOT, shotEventEntity.getIdShot());
        cv.put(TIMESTAMP, shotEventEntity.getTimestamp());
        cv.put(TYPE, shotEventEntity.getType());
        return cv;
    }
}
