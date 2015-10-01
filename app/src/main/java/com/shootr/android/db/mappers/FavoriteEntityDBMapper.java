package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.db.DatabaseContract.FavoriteTable;
import java.util.Date;
import javax.inject.Inject;

public class FavoriteEntityDBMapper {

    @Inject public FavoriteEntityDBMapper() {
    }

    public FavoriteEntity fromCursor(Cursor cursor) {
        FavoriteEntity entity = new FavoriteEntity();
        entity.setIdStream(stringFromColumn(FavoriteTable.ID_STREAM, cursor));
        entity.setOrder(intFromColumn(FavoriteTable.ORDER, cursor));

        entity.setSynchronizedStatus(stringFromColumn(FavoriteTable.SYNCHRONIZED, cursor));

        return entity;
    }

    public ContentValues toContentValues(FavoriteEntity entity) {
        ContentValues values = new ContentValues();
        values.put(FavoriteTable.ID_STREAM, entity.getIdStream());
        values.put(FavoriteTable.ORDER, entity.getOrder());

        values.put(FavoriteTable.SYNCHRONIZED, entity.getSynchronizedStatus());

        return values;
    }

    private Long timestamp(Date date) {
        return date != null ? date.getTime() : null;
    }

    private Date toDate(Long timestamp) {
        return timestamp != null ? new Date(timestamp) : null;
    }

    protected String stringFromColumn(String columnName, Cursor cursor) {
        return cursor.getString(column(columnName, cursor));
    }

    protected Integer intFromColumn(String columnName, Cursor cursor) {
        return cursor.getInt(column(columnName, cursor));
    }

    protected Long longFromColumn(String columnName, Cursor cursor) {
        return cursor.isNull(column(columnName, cursor)) ? null : cursor.getLong(column(columnName, cursor));
    }

    private int column(String id, Cursor cursor) {
        return cursor.getColumnIndex(id);
    }
}
