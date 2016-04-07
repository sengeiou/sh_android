package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import com.shootr.mobile.data.entity.MuteStreamEntity;
import com.shootr.mobile.db.DatabaseContract;

import javax.inject.Inject;

public class MuteStreamEntityDBMapper extends GenericDBMapper {

    public static final String ID_MUTED_STREAM = DatabaseContract.MuteTable.ID_MUTED_STREAM;

    @Inject public MuteStreamEntityDBMapper() {
    }

    public MuteStreamEntity fromCursor(Cursor c) {
        MuteStreamEntity muteStreamEntity = new MuteStreamEntity();
        muteStreamEntity.setIdStream(c.getString(c.getColumnIndex(ID_MUTED_STREAM)));
        setSynchronizedfromCursor(c, muteStreamEntity);
        return muteStreamEntity;
    }

    public ContentValues toContentValues(MuteStreamEntity muteStreamEntity) {
        ContentValues cv = new ContentValues();
        cv.put(ID_MUTED_STREAM, muteStreamEntity.getIdStream());
        setSynchronizedtoContentValues(muteStreamEntity, cv);
        return cv;
    }

}
