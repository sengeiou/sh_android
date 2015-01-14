package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.db.DatabaseContract.MatchTable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MatchMapper extends GenericMapper{

    public ContentValues toContentValues(MatchEntity match){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MatchTable.ID_MATCH, match.getIdMatch());
        contentValues.put(MatchTable.MATCH_DATE, match.getMatchDate()!=null ? match.getMatchDate().getTime() : null);
        contentValues.put(MatchTable.ID_LOCAL_TEAM, match.getIdLocalTeam());
        contentValues.put(MatchTable.ID_VISITOR_TEAM, match.getIdVisitorTeam());
        contentValues.put(MatchTable.LOCAL_TEAM_NAME, match.getLocalTeamName());
        contentValues.put(MatchTable.VISITOR_TEAM_NAME, match.getVisitorTeamName());
        setSynchronizedtoContentValues(match,contentValues);
        return contentValues;
    }


    public MatchEntity fromDto(Map<String, Object> dto) {
        MatchEntity match = new MatchEntity();
        match.setIdMatch((Number)dto.get(MatchTable.ID_MATCH) == null ? null : ((Number) dto.get(MatchTable.ID_MATCH)).longValue());
        match.setIdLocalTeam((Number)dto.get(MatchTable.ID_LOCAL_TEAM) == null ? null :((Number) dto.get(MatchTable.ID_LOCAL_TEAM)).longValue());
        match.setIdVisitorTeam((Number)dto.get(MatchTable.ID_VISITOR_TEAM) == null ? null : ((Number)dto.get(MatchTable.ID_VISITOR_TEAM)).longValue());
        match.setLocalTeamName((String)dto.get(MatchTable.LOCAL_TEAM_NAME) == null ? null : (String)dto.get(MatchTable.LOCAL_TEAM_NAME));
        match.setVisitorTeamName((String)dto.get(MatchTable.VISITOR_TEAM_NAME) == null ? null : (String)dto.get(MatchTable.VISITOR_TEAM_NAME));
        match.setMatchDate(dto.get(MatchTable.MATCH_DATE) == null ? null : new Date((Long)dto.get(MatchTable.MATCH_DATE)));
        setSynchronizedfromDto(dto,match);
        return match;
    }

    public  Map<String, Object> toDto(MatchEntity match) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(MatchTable.ID_MATCH, match == null ? null : match.getIdMatch());
        dto.put(MatchTable.ID_LOCAL_TEAM, match == null ? null : match.getIdLocalTeam());
        dto.put(MatchTable.ID_VISITOR_TEAM, match == null ? null : match.getIdVisitorTeam());
        dto.put(MatchTable.LOCAL_TEAM_NAME, match == null ? null : match.getLocalTeamName());
        dto.put(MatchTable.VISITOR_TEAM_NAME, match == null ? null : match.getVisitorTeamName());
        dto.put(MatchTable.MATCH_DATE, match == null ? null : match.getMatchDate());
        setSynchronizedtoDto(match, dto);
        return dto;
    }

    public MatchEntity fromCursor(Cursor c) {
        MatchEntity match = new MatchEntity();
        match.setIdMatch(c.getLong(c.getColumnIndex(MatchTable.ID_MATCH)));
        match.setIdLocalTeam(c.getLong(c.getColumnIndex(MatchTable.ID_LOCAL_TEAM)));
        match.setIdVisitorTeam(c.getLong(c.getColumnIndex(MatchTable.ID_VISITOR_TEAM)));
        match.setLocalTeamName(c.getString(c.getColumnIndex(MatchTable.LOCAL_TEAM_NAME)));
        match.setVisitorTeamName(c.getString(c.getColumnIndex(MatchTable.VISITOR_TEAM_NAME)));
        Long date = c.getLong(c.getColumnIndex(MatchTable.MATCH_DATE));
        match.setMatchDate(date != 0L ? new Date(date) : null);
        setSynchronizedfromCursor(c, match);
        return match;
    }

}
