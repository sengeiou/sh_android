package com.fav24.shootr.batch.optaData.rest.DTO;

import org.springframework.stereotype.Component;

import com.fav24.shootr.dao.domain.Area;
import com.fav24.shootr.dao.domain.Competition;
import com.fav24.shootr.dao.domain.Match;
import com.fav24.shootr.dao.domain.Season;
import com.fav24.shootr.dao.domain.Team;
import com.fav24.shootr.util.DateUtil;

@Component
public class DTOtoDomainTransformer {

	public Team teamDTOtoDomain(TeamDTO dto, Long idArea) {
		Team domain = new Team();
		domain.setClubName(dto.getClub_name());
		domain.setIdTeamOpta(dto.getTeam_id());
		domain.setOfficialName(dto.getOfficial_name());
		domain.setShortName(dto.getShort_name());
		domain.setTlaName(dto.getTla_name());
		domain.setIdArea(idArea);
		domain.setType(dto.getType());
		return domain;
	}

	public Competition competitionDTOtoDomain(CompetitionDTO dto) {
		Competition domain = new Competition();
		domain.setIdCompetitionOpta(dto.getCompetition_id());
		domain.setIdArea(dto.getArea_id());
		domain.setName(dto.getName());
		domain.setFormat(dto.getFormat());
		domain.setDisplayOrder(dto.getDisplay_order());
		domain.setType(dto.getType());
		return domain;
	}

	public Area areaDTOtoDomain(AreaDTO dto) {
		Area domain = new Area();
		domain.setIdAreaOpta(dto.getArea_id());
		domain.setName(dto.getName());
		return domain;
	}

	public Season seasonDTOtoDomain(SeasonDTO dto, Long idCompetition, Long idSeason) {
		Season domain = new Season();
		domain.setIdSeason(idSeason);
		domain.setIdSeasonOpta(dto.getSeason_id());
		domain.setIdCompetition(idCompetition);
		domain.setName(dto.getName());
		domain.setStartDate(DateUtil.format(dto.getStart_date(), "yyyy-MM-dd", null));
		domain.setEndDate(DateUtil.format(dto.getEnd_date(), "yyyy-MM-dd", null));
		domain.setLastUpdated(DateUtil.format(dto.getLast_updated(), "yyyy-MM-dd HH:mm:ss", "CET"));
		return domain;
	}

	public Match matchDTOtoDomain(MatchDTO dto, Long idSeason, Long idMatch) {
		Match domain = new Match();
		domain.setIdMatch(idMatch);
		domain.setIdMatchOpta(dto.getMatch_id());
		domain.setIdSeason(idSeason);
		domain.setDateMatch(DateUtil.format(dto.getDate_utc()+" "+dto.getTime_utc(), "yyyy-MM-dd HH:mm:ss", "CET"));
		domain.setIdTeamA(dto.getTeam_A_id());
		domain.setIdTeamB(dto.getTeam_B_id());
		domain.setTeamAName(dto.getTeam_A_name());
		domain.setTeamBName(dto.getTeam_B_name());
		domain.setStatus(dto.getStatus());
		domain.setGameweek(dto.getGameweek());
		domain.setWinner(dto.getWinner());
		domain.setFsA(dto.getFs_A());
		domain.setFsB(dto.getFs_B());
		domain.setHtsA(dto.getHts_A());
		domain.setHtsB(dto.getHts_B());
		domain.setEtsA(dto.getEts_A());
		domain.setEtsB(dto.getEts_B());
		domain.setPsA(dto.getPs_A());
		domain.setPsB(dto.getPs_B());
		domain.setLastUpdated(DateUtil.format(dto.getLast_updated(), "yyyy-MM-dd HH:mm:ss", "CET"));
		domain.setMinute(dto.getMinute());
		domain.setMinuteExtra(dto.getMinute_extra());
		domain.setMatchPeriod(dto.getMatch_period());
		return domain;
	}
}
