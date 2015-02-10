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
        contentValues.put(WatchTable.ID_EVENT, watchEntity.getIdEvent());
        contentValues.put(WatchTable.ID_USER, watchEntity.getIdUser());
        contentValues.put(WatchTable.PLACE, watchEntity.getPlace());
        contentValues.put(WatchTable.VISIBLE, watchEntity.isVisible() ? 1 : 0);
        setSynchronizedtoContentValues(watchEntity, contentValues);
        return contentValues;
    }

    public WatchEntity fromDto(Map<String, Object> dto) {
        WatchEntity watch = new WatchEntity();
        watch.setIdEvent(
          dto.get(WatchTable.ID_EVENT) == null ? null : ((Number) dto.get(WatchTable.ID_EVENT)).longValue());
        watch.setIdUser(dto.get(WatchTable.ID_USER) == null ? null : ((Number) dto.get(WatchTable.ID_USER)).longValue());
        watch.setPlace((String) dto.get(WatchTable.PLACE));
        watch.setVisible(dto.get(WatchTable.VISIBLE) == null ? null : dto.get(WatchTable.VISIBLE).equals(1));
        setSynchronizedfromDto(dto, watch);
        return watch;
    }

    public Map<String, Object> toDto(WatchEntity watch) {
        Map<String, Object> dto = new HashMap<>();
        dto.put(WatchTable.ID_EVENT, watch == null ? null : watch.getIdEvent());
        dto.put(WatchTable.ID_USER, watch == null ? null : watch.getIdUser());
        dto.put(WatchTable.PLACE, watch == null ? null : watch.getPlace());
        dto.put(WatchTable.VISIBLE, watch == null ? null : watch.isVisible() ? 1 : 0);
        setSynchronizedtoDto(watch, dto);
        return dto;
    }

    public WatchEntity fromCursor(Cursor c) {
        WatchEntity watch = new WatchEntity();
        watch.setIdEvent(c.getLong(c.getColumnIndex(WatchTable.ID_EVENT)));
        watch.setIdUser(c.getLong(c.getColumnIndex(WatchTable.ID_USER)));
        watch.setPlace(c.getString(c.getColumnIndex(WatchTable.PLACE)));
        watch.setVisible(c.getLong(c.getColumnIndex(WatchTable.VISIBLE)) == 1);
        setSynchronizedfromCursor(c, watch);
        return watch;
    }
}
