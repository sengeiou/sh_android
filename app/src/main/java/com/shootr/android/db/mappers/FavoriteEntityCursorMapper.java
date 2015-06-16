package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.FavoriteEntity;
import com.shootr.android.db.DatabaseContract.FavoriteTable;
import java.util.Date;
import javax.inject.Inject;

public class FavoriteEntityCursorMapper {

    @Inject public FavoriteEntityCursorMapper() {
    }

    public FavoriteEntity fromCursor(Cursor cursor) {
        FavoriteEntity entity = new FavoriteEntity();
        entity.setIdEvent(stringFromColumn(FavoriteTable.ID_EVENT, cursor));
        entity.setOrder(intFromColumn(FavoriteTable.ORDER, cursor));

        entity.setBirth(toDate(longFromColumn(FavoriteTable.BIRTH, cursor)));
        entity.setModified(toDate(longFromColumn(FavoriteTable.MODIFIED, cursor)));
        entity.setDeleted(toDate(longFromColumn(FavoriteTable.DELETED, cursor)));
        entity.setSynchronizedStatus(stringFromColumn(FavoriteTable.SYNCHRONIZED, cursor));
        entity.setRevision(intFromColumn(FavoriteTable.REVISION, cursor));

        return entity;
    }

    public ContentValues toContentValues(FavoriteEntity entity) {
        ContentValues values = new ContentValues();
        values.put(FavoriteTable.ID_EVENT, entity.getIdEvent());
        values.put(FavoriteTable.ORDER, entity.getOrder());

        values.put(FavoriteTable.BIRTH, timestamp(entity.getBirth()));
        values.put(FavoriteTable.MODIFIED, timestamp(entity.getModified()));
        values.put(FavoriteTable.DELETED, timestamp(entity.getDeleted()));
        values.put(FavoriteTable.SYNCHRONIZED, entity.getSynchronizedStatus());
        values.put(FavoriteTable.REVISION, entity.getRevision());

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
