package com.fav24.shootr.batch.optaData.rest.DTO;

public class TeamDTO {

	private Long team_id; // si
	private Long ow_team_id; // no
	private String type; // si
	private String soccertype; // no
	private String teamtype; // filtrar por default, no se guarda
	private String club_name; // si
	private String official_name; // si
	private String short_name; // si
	private String tla_name; // si
	private String last_updated; // si

	public TeamDTO(Long team_id, Long ow_team_id, String type, String soccertype, String teamtype, String club_name, String official_name, String short_name, String tla_name, String last_updated) {
		this.team_id = team_id;
		this.ow_team_id = ow_team_id;
		this.type = type;
		this.soccertype = soccertype;
		this.teamtype = teamtype;
		this.club_name = club_name;
		this.official_name = official_name;
		this.short_name = short_name;
		this.tla_name = tla_name;
		this.last_updated = last_updated;
	}

	public TeamDTO() {
	}

	public String getOfficial_name() {
		return official_name;
	}

	public void setOfficial_name(String official_name) {
		this.official_name = official_name;
	}

	public String getShort_name() {
		return short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	public String getTla_name() {
		return tla_name;
	}

	public void setTla_name(String tla_name) {
		this.tla_name = tla_name;
	}

	public Long getTeam_id() {
		return team_id;
	}

	public void setTeam_id(Long team_id) {
		this.team_id = team_id;
	}

	public Long getOw_team_id() {
		return ow_team_id;
	}

	public void setOw_team_id(Long ow_team_id) {
		this.ow_team_id = ow_team_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSoccertype() {
		return soccertype;
	}

	public void setSoccertype(String soccertype) {
		this.soccertype = soccertype;
	}

	public String getTeamtype() {
		return teamtype;
	}

	public void setTeamtype(String teamtype) {
		this.teamtype = teamtype;
	}

	public String getClub_name() {
		return club_name;
	}

	public void setClub_name(String club_name) {
		this.club_name = club_name;
	}

	public String getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(String last_updated) {
		this.last_updated = last_updated;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TeamDTO [team_id=").append(team_id).append(", ow_team_id=").append(ow_team_id).append(", type=").append(type).append(", soccertype=").append(soccertype).append(", teamtype=")
				.append(teamtype).append(", club_name=").append(club_name).append(", official_name=").append(official_name).append(", short_name=").append(short_name).append(", tla_name=")
				.append(tla_name).append(", last_updated=").append(last_updated).append("]");
		return builder.toString();
	}

}
