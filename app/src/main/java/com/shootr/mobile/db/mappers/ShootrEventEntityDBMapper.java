package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.ShootrEventEntity;
import com.shootr.mobile.db.DatabaseContract;
import javax.inject.Inject;

public class ShootrEventEntityDBMapper extends GenericDBMapper {

    public static final String ID_KEY = DatabaseContract.ShootrEventTable.ID_KEY;
    public static final String TIMESTAMP = DatabaseContract.ShootrEventTable.TIMESTAMP;
    public static final String TYPE = DatabaseContract.ShootrEventTable.TYPE;

    @Inject public ShootrEventEntityDBMapper() {
    }

    public ShootrEventEntity fromCursor(Cursor c) {
        ShootrEventEntity shootrEventEntity = new ShootrEventEntity();
        shootrEventEntity.setId(c.getString(c.getColumnIndex(ID_KEY)));
        shootrEventEntity.setType(c.getString(c.getColumnIndex(TYPE)));
        shootrEventEntity.setTimestamp(c.getLong(c.getColumnIndex(TIMESTAMP)));
        return shootrEventEntity;
    }

    public ContentValues toContentValues(ShootrEventEntity shootrEventEntity) {
        ContentValues cv = new ContentValues();
        cv.put(ID_KEY, shootrEventEntity.getId());
        cv.put(TIMESTAMP, shootrEventEntity.getTimestamp());
        cv.put(TYPE, shootrEventEntity.getType());
        return cv;
    }
}
