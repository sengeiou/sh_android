package com.shootr.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;
import com.shootr.android.db.objects.TeamEntity;
import com.shootr.android.db.DatabaseContract.TeamTable;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class TeamMapper extends GenericMapper {

    @Inject public TeamMapper() {
    }

    @Deprecated
    /**
     * @deprecated TeamEntity is not used for database
     */
    public ContentValues toContentValues(TeamEntity teamEntity)  {
        throw new RuntimeException("TeamEntity is not used for database");
    }


    public TeamEntity fromDto(Map<String, Object> dto) {
        TeamEntity team = new TeamEntity();
        team.setIdTeam(dto.get(TeamTable.ID_TEAM) == null ? null : ((Number)dto.get(TeamTable.ID_TEAM)).longValue());
        team.setPopularity(dto.get(TeamTable.POPULARITY) == null ? null : ((Number)dto.get(TeamTable.ID_TEAM)).intValue());
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
        dto.put(TeamTable.POPULARITY, team == null ? null : team.getPopularity());
        dto.put(TeamTable.TLA_NAME, team == null ? null : team.getTlaName());
        dto.put(TeamTable.OFFICIAL_NAME, team == null ? null : team.getOfficialName());
        dto.put(TeamTable.CLUB_NAME, team == null ? null : team.getClubName());
        dto.put(TeamTable.SHORT_NAME, team == null ? null : team.getShortName());
        setSynchronizedtoDto(team, dto);
        return dto;
    }

    @Deprecated
    /**
     * @deprecated TeamEntity is not used for database
     */
    public TeamEntity fromCursor(Cursor c) {
        throw new RuntimeException("TeamEntity is not used for database");
    }

}
