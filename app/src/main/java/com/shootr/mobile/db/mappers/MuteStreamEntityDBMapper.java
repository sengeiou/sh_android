package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.db.DatabaseContract;

public class MuteStreamEntityDBMapper extends GenericDBMapper {

    public static final String ID_MUTED_STREAM = DatabaseContract.MuteTable.ID_MUTED_STREAM;

    public MuteStreamEntity fromCursor(Cursor c) {
        MuteStreamEntity muteStreamEntity = new MuteStreamEntity();
        muteStreamEntity.setIdStream(c.getString(c.getColumnIndex(ID_MUTED_STREAM)));
        return muteStreamEntity;
    }

    public ContentValues toContentValues(MuteStreamEntity muteStreamEntity) {
        ContentValues cv = new ContentValues();
        cv.put(ID_MUTED_STREAM, muteStreamEntity.getIdStream());
        return cv;
    }

}
