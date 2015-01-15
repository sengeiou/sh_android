package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.db.DatabaseContract.WatchTable;
import java.util.HashMap;
import java.util.Map;

public class WatchMapper extends GenericMapper {

    public ContentValues toContentValues(WatchEntity watchEntity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WatchTable.ID_MATCH, watchEntity.getIdMatch());
        contentValues.put(WatchTable.ID_USER, watchEntity.getIdUser());
        contentValues.put(WatchTable.STATUS, watchEntity.getStatus());
        contentValues.put(WatchTable.PLACE, watchEntity.getPlace());
        contentValues.put(WatchTable.VISIBLE, watchEntity.getVisible() ? 1 : 0);
        contentValues.put(WatchTable.NOTIFICATION, watchEntity.getNotification());
        setSynchronizedtoContentValues(watchEntity, contentValues);
        return contentValues;
    }

    public WatchEntity fromDto(Map<String, Object> dto) {
        WatchEntity watch = new WatchEntity();
        watch.setIdMatch(
          dto.get(WatchTable.ID_MATCH) == null ? null : ((Number) dto.get(WatchTable.ID_MATCH)).longValue());
        watch.setIdUser(dto.get(WatchTable.ID_USER) == null ? null : ((Number) dto.get(WatchTable.ID_USER)).longValue());
        watch.setStatus(dto.get(WatchTable.STATUS) == null ? null : ((Number) dto.get(WatchTable.STATUS)).longValue());
        watch.setPlace((String) dto.get(WatchTable.PLACE));
        watch.setVisible(dto.get(WatchTable.VISIBLE) == null ? null : dto.get(WatchTable.VISIBLE).equals(1));
        watch.setNotification(
          dto.get(WatchTable.NOTIFICATION) == null ? null : ((Number) dto.get(WatchTable.NOTIFICATION)).intValue());
        setSynchronizedfromDto(dto, watch);
        return watch;
    }

    public Map<String, Object> toDto(WatchEntity watch) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(WatchTable.ID_MATCH, watch == null ? null : watch.getIdMatch());
        dto.put(WatchTable.ID_USER, watch == null ? null : watch.getIdUser());
        dto.put(WatchTable.STATUS, watch == null ? null : watch.getStatus());
        dto.put(WatchTable.PLACE, watch == null ? null : watch.getPlace());
        dto.put(WatchTable.VISIBLE, watch == null ? null : watch.getVisible() ? 1 : 0);
        dto.put(WatchTable.NOTIFICATION, watch == null ? null : watch.getNotification());
        setSynchronizedtoDto(watch, dto);
        return dto;
    }

    public WatchEntity fromCursor(Cursor c) {
        WatchEntity watch = new WatchEntity();
        watch.setIdMatch(c.getLong(c.getColumnIndex(WatchTable.ID_MATCH)));
        watch.setIdUser(c.getLong(c.getColumnIndex(WatchTable.ID_USER)));
        watch.setStatus(c.getLong(c.getColumnIndex(WatchTable.STATUS)));
        watch.setPlace(c.getString(c.getColumnIndex(WatchTable.PLACE)));
        watch.setVisible(c.getLong(c.getColumnIndex(WatchTable.VISIBLE)) == 1);
        watch.setNotification(c.getInt(c.getColumnIndex(WatchTable.NOTIFICATION)));
        setSynchronizedfromCursor(c, watch);
        return watch;
    }
}
