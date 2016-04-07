package com.shootr.mobile.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import com.shootr.mobile.data.entity.FavoriteEntity;
import com.shootr.mobile.db.DatabaseContract;

import javax.inject.Inject;

public class FavoriteEntityDBMapper {

    @Inject public FavoriteEntityDBMapper() {
    }

    public FavoriteEntity fromCursor(Cursor cursor) {
        FavoriteEntity entity = new FavoriteEntity();
        entity.setIdStream(stringFromColumn(DatabaseContract.FavoriteTable.ID_STREAM, cursor));
        entity.setOrder(intFromColumn(DatabaseContract.FavoriteTable.ORDER, cursor));

        entity.setSynchronizedStatus(stringFromColumn(DatabaseContract.FavoriteTable.SYNCHRONIZED, cursor));

        return entity;
    }

    public ContentValues toContentValues(FavoriteEntity entity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.FavoriteTable.ID_STREAM, entity.getIdStream());
        values.put(DatabaseContract.FavoriteTable.ORDER, entity.getOrder());

        values.put(DatabaseContract.FavoriteTable.SYNCHRONIZED, entity.getSynchronizedStatus());

        return values;
    }

    protected String stringFromColumn(String columnName, Cursor cursor) {
        return cursor.getString(column(columnName, cursor));
    }

    protected Integer intFromColumn(String columnName, Cursor cursor) {
        return cursor.getInt(column(columnName, cursor));
    }

    private int column(String id, Cursor cursor) {
        return cursor.getColumnIndex(id);
    }
}
