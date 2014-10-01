package gm.mobi.android.db.mappers;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.objects.Team;

public class TeamMapper extends  GenericMapper{

    public static Team fromCursor(Cursor c){
        Team team = new Team();
        team.setIdTeam(c.getLong(c.getColumnIndex(GMContract.TeamTable.ID_TEAM)));
        team.setClubName(c.getString(c.getColumnIndex(GMContract.TeamTable.CLUB_NAME)));
        team.setOfficialName(c.getString(c.getColumnIndex(GMContract.TeamTable.OFFICIAL_NAME)));
        team.setShortName(c.getString(c.getColumnIndex(GMContract.TeamTable.SHORT_NAME)));
        team.setTlaName(c.getString(c.getColumnIndex(GMContract.TeamTable.TLA_NAME)));
        setSynchronizedFromCursor(c,team);
        return team;
    }

    public static ContentValues toContentValues(Team team){
        ContentValues cv = new ContentValues();
        cv.put(GMContract.TeamTable.ID_TEAM, team.getIdTeam());
        cv.put(GMContract.TeamTable.CLUB_NAME, team.getClubName());
        cv.put(GMContract.TeamTable.OFFICIAL_NAME, team.getOfficialName());
        cv.put(GMContract.TeamTable.SHORT_NAME, team.getShortName());
        cv.put(GMContract.TeamTable.TLA_NAME, team.getTlaName());
        setSynchronizedToContentValues(cv,team);
        return cv;
    }

    public static Team fromDto(Map<String,Object> dto){
        Team team = new Team();
        team.setIdTeam(((Number)dto.get(GMContract.TeamTable.ID_TEAM)).longValue());
        team.setClubName((String)dto.get(GMContract.TeamTable.CLUB_NAME));
        team.setOfficialName((String)dto.get(GMContract.TeamTable.OFFICIAL_NAME));
        team.setShortName((String)dto.get(GMContract.TeamTable.SHORT_NAME));
        team.setTlaName((String)dto.get(GMContract.TeamTable.TLA_NAME));
        setSynchronizedFromDto(dto,team);
        return team;
    }

    public static Map<String,Object> toDto(Team team){
        Map<String,Object> dto = new HashMap<>();
        dto.put(GMContract.TeamTable.ID_TEAM, team == null ? null : team.getIdTeam());
        dto.put(GMContract.TeamTable.OFFICIAL_NAME, team == null ? null : team.getOfficialName());
        dto.put(GMContract.TeamTable.CLUB_NAME, team == null ? null : team.getClubName());
        dto.put(GMContract.TeamTable.SHORT_NAME, team == null ? null : team.getShortName());
        dto.put(GMContract.TeamTable.TLA_NAME, team == null ? null : team.getTlaName());
        setSynchronizedToDto(dto, team);
        return dto;
    }



}
