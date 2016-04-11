package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import com.shootr.mobile.data.entity.BanEntity;
import com.shootr.mobile.db.DatabaseContract;

public class BanEntityDBMapper extends GenericDBMapper {

    public static final String ID_USER = DatabaseContract.BanTable.ID_USER;
    public static final String ID_BANNED_USER = DatabaseContract.BanTable.ID_BANNED_USER;

    public BanEntity fromCursor(Cursor c) {
        BanEntity banEntity = new BanEntity();
        banEntity.setIdUser(c.getString(c.getColumnIndex(ID_USER)));
        banEntity.setIdBannedUser(c.getString(c.getColumnIndex(ID_BANNED_USER)));
        return banEntity;
    }

    public ContentValues toContentValues(BanEntity banEntity) {
        ContentValues cv = new ContentValues();
        cv.put(ID_USER, banEntity.getIdUser());
        cv.put(ID_BANNED_USER, banEntity.getIdBannedUser());
        return cv;
    }

}
