package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.ShootrEventEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class ShotEventEntityDBMapper extends GenericDBMapper {

    public static final String ID_SHOT = DatabaseContract.ShotEventTable.ID_SHOT;
    public static final String TIMESTAMP = DatabaseContract.ShotEventTable.TIMESTAMP;
    public static final String TYPE = DatabaseContract.ShotEventTable.TYPE;

    @Inject public ShotEventEntityDBMapper() {
    }

    public ShootrEventEntity fromCursor(Cursor c) {
        ShootrEventEntity shootrEventEntity = new ShootrEventEntity();
        shootrEventEntity.setIdShot(c.getString(c.getColumnIndex(ID_SHOT)));
        shootrEventEntity.setType(c.getString(c.getColumnIndex(TYPE)));
        shootrEventEntity.setTimestamp(c.getLong(c.getColumnIndex(TIMESTAMP)));
        return shootrEventEntity;
    }

    public ContentValues toContentValues(ShootrEventEntity shootrEventEntity) {
        ContentValues cv = new ContentValues();
        cv.put(ID_SHOT, shootrEventEntity.getIdShot());
        cv.put(TIMESTAMP, shootrEventEntity.getTimestamp());
        cv.put(TYPE, shootrEventEntity.getType());
        return cv;
    }
}
