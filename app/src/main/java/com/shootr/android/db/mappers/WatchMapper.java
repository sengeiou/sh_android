package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.db.DatabaseContract.WatchTable;
import java.util.HashMap;
import java.util.Map;

public class WatchMapper extends GenericMapper {

    public ContentValues toContentValues(WatchEntity watchEntity){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WatchTable.ID_MATCH,watchEntity.getIdMatch());
        contentValues.put(WatchTable.ID_USER, watchEntity.getIdUser());
        contentValues.put(WatchTable.STATUS, watchEntity.getStatus());
        setSynchronizedtoContentValues(watchEntity,contentValues);
        return contentValues;
    }


    public WatchEntity fromDto(Map<String, Object> dto) {
        WatchEntity watch = new WatchEntity();
        watch.setIdMatch(
          (Number) dto.get(WatchTable.ID_MATCH) == null ? null : ((Number) dto.get(WatchTable.ID_MATCH)).longValue());
        watch.setIdUser(
          (Number) dto.get(WatchTable.ID_USER) == null ? null : ((Number) dto.get(WatchTable.ID_USER)).longValue());
        watch.setStatus((Number)dto.get(WatchTable.STATUS) == null ? null : ((Number) dto.get(WatchTable.STATUS)).longValue());
        setSynchronizedfromDto(dto,watch);
        return watch;
    }

    public  Map<String, Object> toDto(WatchEntity watch) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(WatchTable.ID_MATCH, watch == null ? null : watch.getIdMatch());
        dto.put(WatchTable.ID_USER, watch == null ? null : watch.getIdUser());
        dto.put(WatchTable.STATUS, watch == null ? null : watch.getStatus());
        setSynchronizedtoDto(watch, dto);
        return dto;
    }

    public WatchEntity fromCursor(Cursor c) {
        WatchEntity watch = new WatchEntity();
        watch.setIdMatch(c.getLong(c.getColumnIndex(WatchTable.ID_MATCH)));
        watch.setIdUser(c.getLong(c.getColumnIndex(WatchTable.ID_USER)));
        watch.setStatus(c.getLong(c.getColumnIndex(WatchTable.STATUS)));
        setSynchronizedfromCursor(c, watch);
        return watch;
    }


}
