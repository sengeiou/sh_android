package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.db.objects.TeamEntity;
import com.shootr.android.db.DatabaseContract.TeamTable;
import java.util.HashMap;
import java.util.Map;

public class TeamMapper extends GenericMapper {

    public ContentValues toContentValues(TeamEntity teamEntity){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TeamTable.ID_TEAM,teamEntity.getIdTeam());
        contentValues.put(TeamTable.TLA_NAME, teamEntity.getTlaName());
        contentValues.put(TeamTable.SHORT_NAME, teamEntity.getShortName());
        contentValues.put(TeamTable.CLUB_NAME, teamEntity.getClubName());
        contentValues.put(TeamTable.OFFICIAL_NAME, teamEntity.getOfficialName());
        setSynchronizedtoContentValues(teamEntity, contentValues);
        return contentValues;
    }


    public TeamEntity fromDto(Map<String, Object> dto) {
        TeamEntity team = new TeamEntity();
        team.setIdTeam(dto.get(TeamTable.ID_TEAM) == null ? null : ((Number)dto.get(TeamTable.ID_TEAM)).longValue());
        team.setClubName(dto.get(TeamTable.CLUB_NAME) == null ? null : (String)dto.get(TeamTable.CLUB_NAME));
        team.setOfficialName(dto.get(TeamTable.OFFICIAL_NAME) == null ? null : (String) dto.get(TeamTable.OFFICIAL_NAME));
        team.setShortName(dto.get(TeamTable.SHORT_NAME) == null ? null : (String) dto.get(TeamTable.SHORT_NAME));
        team.setTlaName(dto.get(TeamTable.TLA_NAME) == null ? null : (String) dto.get(TeamTable.TLA_NAME));
        setSynchronizedfromDto(dto,team);
        return team;
    }

    public  Map<String, Object> toDto(TeamEntity team) {
        Map<String,Object> dto = new HashMap<>();
        dto.put(TeamTable.ID_TEAM, team == null ? null : team.getIdTeam());
        dto.put(TeamTable.TLA_NAME, team == null ? null : team.getTlaName());
        dto.put(TeamTable.OFFICIAL_NAME, team == null ? null : team.getOfficialName());
        dto.put(TeamTable.CLUB_NAME, team == null ? null : team.getClubName());
        dto.put(TeamTable.SHORT_NAME, team == null ? null : team.getShortName());
        setSynchronizedtoDto(team, dto);
        return dto;
    }

    public TeamEntity fromCursor(Cursor c) {
        TeamEntity team = new TeamEntity();
        team.setIdTeam(c.getLong(c.getColumnIndex(TeamTable.ID_TEAM)));
        team.setTlaName(c.getString(c.getColumnIndex(TeamTable.TLA_NAME)));
        team.setShortName(c.getString(c.getColumnIndex(TeamTable.SHORT_NAME)));
        team.setOfficialName(c.getString(c.getColumnIndex(TeamTable.OFFICIAL_NAME)));
        team.setClubName(c.getString(c.getColumnIndex(TeamTable.CLUB_NAME)));
        setSynchronizedfromCursor(c, team);
        return team;
    }

}
