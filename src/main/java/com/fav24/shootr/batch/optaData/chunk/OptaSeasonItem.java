package com.fav24.shootr.batch.optaData.chunk;

import java.util.ArrayList;
import java.util.List;

import com.fav24.shootr.batch.optaData.rest.DTO.SeasonDTO;
import com.fav24.shootr.dao.domain.Competition;
import com.fav24.shootr.dao.domain.Season;

public class OptaSeasonItem {
	private Long idCompetitionOpta;
	private Competition competition;
	private List<SeasonDTO> dto;
	private List<Season> newSeasons;
	private List<Season> updateSeasons;

	public OptaSeasonItem() {
		newSeasons = new ArrayList<Season>();
		updateSeasons = new ArrayList<Season>();
	}

	public Long getIdCompetitionOpta() {
		return idCompetitionOpta;
	}

	public void setIdCompetitionOpta(Long idCompetitionOpta) {
		this.idCompetitionOpta = idCompetitionOpta;
	}

	public Competition getCompetition() {
		return competition;
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}

	public List<SeasonDTO> getDto() {
		return dto;
	}

	public void setDto(List<SeasonDTO> dto) {
		this.dto = dto;
	}

	public List<Season> getNewSeasons() {
		return newSeasons;
	}

	public void setNewSeasons(List<Season> newSeasons) {
		this.newSeasons = newSeasons;
	}

	public List<Season> getUpdateSeasons() {
		return updateSeasons;
	}

	public void setUpdateSeasons(List<Season> updateSeasons) {
		this.updateSeasons = updateSeasons;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OptaSeasonItem [idCompetitionOpta=").append(idCompetitionOpta).append(", competition=").append(competition).append(", dto=").append(dto).append(", newSeasons=")
				.append(newSeasons).append(", updateSeasons=").append(updateSeasons).append("]");
		return builder.toString();
	}

}
