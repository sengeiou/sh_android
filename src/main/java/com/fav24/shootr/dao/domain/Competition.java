package com.fav24.shootr.dao.domain;

public class Competition {

	private Long idCompetition;
	private Long idCompetitionOpta;
	private Long idArea;
	private String name;
	/**
	 * Determines if a competition is played between teams of 1 country (domestic) or teams from multiple countries (international). International super cup International cup Domestic super cup
	 * Domestic cup Domestic league
	 */
	private String format;
	/**
	 * Can be used to sort competitions on level of importance within the same area. Sorting direction is ascending.
	 */
	private Long displayOrder;
	/**
	 * Determines if a competition is between club teams (club) or national teams (international). international - between national teams, for example 'World Cup'. club - between club teams, for
	 * example 'Arsenal'.
	 */
	private String type;
	private String lastUpdated;

	public Long getIdCompetition() {
		return idCompetition;
	}

	public void setIdCompetition(Long idCompetition) {
		this.idCompetition = idCompetition;
	}

	public Long getIdCompetitionOpta() {
		return idCompetitionOpta;
	}

	public void setIdCompetitionOpta(Long idCompetitionOpta) {
		this.idCompetitionOpta = idCompetitionOpta;
	}

	public Long getIdArea() {
		return idArea;
	}

	public void setIdArea(Long idArea) {
		this.idArea = idArea;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
		builder.append("Competition [idCompetition=").append(idCompetition).append(", idCompetitionOpta=").append(idCompetitionOpta).append(", idArea=").append(idArea).append(", name=").append(name)
				.append(", format=").append(format).append(", displayOrder=").append(displayOrder).append(", type=").append(type).append(", lastUpdated=").append(lastUpdated).append("]");
		return builder.toString();
	}

}
