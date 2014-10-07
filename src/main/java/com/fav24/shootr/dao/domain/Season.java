package com.fav24.shootr.dao.domain;

public class Season extends Synchronized {

	private Long idSeason;
	private Long idSeasonOpta;
	private Long idCompetition;
	private String name;

	private String startDate;
	private String endDate;

	private String lastUpdated;

	public Long getIdSeason() {
		return idSeason;
	}

	public void setIdSeason(Long idSeason) {
		this.idSeason = idSeason;
	}

	public Long getIdSeasonOpta() {
		return idSeasonOpta;
	}

	public void setIdSeasonOpta(Long idSeasonOpta) {
		this.idSeasonOpta = idSeasonOpta;
	}

	public Long getIdCompetition() {
		return idCompetition;
	}

	public void setIdCompetition(Long idCompetition) {
		this.idCompetition = idCompetition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Season [idSeason=").append(idSeason).append(", idSeasonOpta=").append(idSeasonOpta).append(", idCompetition=").append(idCompetition).append(", name=").append(name)
				.append(", startDate=").append(startDate).append(", endDate=").append(endDate).append(", lastUpdated=").append(lastUpdated).append("]");
		return builder.toString();
	}

}
