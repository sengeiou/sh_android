package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.db.DatabaseContract;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventEntityMapper extends GenericMapper{

    public ContentValues toContentValues(EventEntity eventEntity){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.EventTable.ID_EVENT, eventEntity.getIdEvent());
        contentValues.put(DatabaseContract.EventTable.BEGIN_DATE, eventEntity.getBeginDate()!=null ? eventEntity.getBeginDate().getTime() : null);
        contentValues.put(DatabaseContract.EventTable.END_DATE, eventEntity.getEndDate()!=null ? eventEntity.getEndDate().getTime() : null);
        contentValues.put(DatabaseContract.EventTable.ID_LOCAL_TEAM, eventEntity.getIdLocalTeam());
        contentValues.put(DatabaseContract.EventTable.ID_VISITOR_TEAM, eventEntity.getIdVisitorTeam());
        contentValues.put(DatabaseContract.EventTable.LOCAL_TEAM_NAME, eventEntity.getLocalTeamName());
        contentValues.put(DatabaseContract.EventTable.VISITOR_TEAM_NAME, eventEntity.getVisitorTeamName());
        setSynchronizedtoContentValues(eventEntity,contentValues);
        return contentValues;
    }


    public EventEntity fromDto(Map<String, Object> dto) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setIdEvent((Number) dto.get(DatabaseContract.EventTable.ID_EVENT) == null ? null
          : ((Number) dto.get(DatabaseContract.EventTable.ID_EVENT)).longValue());
        eventEntity.setIdLocalTeam((Number) dto.get(DatabaseContract.EventTable.ID_LOCAL_TEAM) == null ? null
          : ((Number) dto.get(DatabaseContract.EventTable.ID_LOCAL_TEAM)).longValue());
        eventEntity.setIdVisitorTeam((Number) dto.get(DatabaseContract.EventTable.ID_VISITOR_TEAM) == null ? null
          : ((Number) dto.get(DatabaseContract.EventTable.ID_VISITOR_TEAM)).longValue());
        eventEntity.setLocalTeamName((String) dto.get(DatabaseContract.EventTable.LOCAL_TEAM_NAME) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.LOCAL_TEAM_NAME));
        eventEntity.setVisitorTeamName((String) dto.get(DatabaseContract.EventTable.VISITOR_TEAM_NAME) == null ? null
          : (String) dto.get(DatabaseContract.EventTable.VISITOR_TEAM_NAME));
        eventEntity.setBeginDate(dto.get(DatabaseContract.EventTable.BEGIN_DATE) == null ? null
          : new Date((Long) dto.get(DatabaseContract.EventTable.BEGIN_DATE)));
        eventEntity.setEndDate(dto.get(DatabaseContract.EventTable.END_DATE) == null ? null
          : new Date((Long) dto.get(DatabaseContract.EventTable.END_DATE)));
        setSynchronizedfromDto(dto,eventEntity);
        return eventEntity;
    }

    public  Map<String, Object> toDto(EventEntity eventEntity) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(DatabaseContract.EventTable.ID_EVENT, eventEntity == null ? null : eventEntity.getIdEvent());
        dto.put(DatabaseContract.EventTable.ID_LOCAL_TEAM, eventEntity == null ? null : eventEntity.getIdLocalTeam());
        dto.put(DatabaseContract.EventTable.ID_VISITOR_TEAM, eventEntity == null ? null : eventEntity.getIdVisitorTeam());
        dto.put(DatabaseContract.EventTable.LOCAL_TEAM_NAME, eventEntity == null ? null : eventEntity.getLocalTeamName());
        dto.put(DatabaseContract.EventTable.VISITOR_TEAM_NAME, eventEntity == null ? null : eventEntity.getVisitorTeamName());
        dto.put(DatabaseContract.EventTable.BEGIN_DATE, eventEntity == null ? null : eventEntity.getBeginDate());
        dto.put(DatabaseContract.EventTable.END_DATE, eventEntity == null ? null : eventEntity.getEndDate());
        setSynchronizedtoDto(eventEntity, dto);
        return dto;
    }

    public EventEntity fromCursor(Cursor c) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setIdEvent(c.getLong(c.getColumnIndex(DatabaseContract.EventTable.ID_EVENT)));
        eventEntity.setIdLocalTeam(c.getLong(c.getColumnIndex(DatabaseContract.EventTable.ID_LOCAL_TEAM)));
        eventEntity.setIdVisitorTeam(c.getLong(c.getColumnIndex(DatabaseContract.EventTable.ID_VISITOR_TEAM)));
        eventEntity.setLocalTeamName(c.getString(c.getColumnIndex(DatabaseContract.EventTable.LOCAL_TEAM_NAME)));
        eventEntity.setVisitorTeamName(c.getString(c.getColumnIndex(DatabaseContract.EventTable.VISITOR_TEAM_NAME)));
        Long date = c.getLong(c.getColumnIndex(DatabaseContract.EventTable.BEGIN_DATE));
        eventEntity.setBeginDate(date != 0L ? new Date(date) : null);
        date = c.getLong(c.getColumnIndex(DatabaseContract.EventTable.END_DATE));
        eventEntity.setEndDate(date != 0L ? new Date(date) : null);
        setSynchronizedfromCursor(c, eventEntity);
        return eventEntity;
    }

}
