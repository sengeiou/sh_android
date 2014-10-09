package com.fav24.shootr.dao.domain;

import java.util.Date;

public class Match {

	/**
	 * identificador del partido
	 */
	private Long idMatch;
	/**
	 * identificador del partido de opta
	 */
	private Long idMatchOpta;
	/**
	 * identificador de la temporada
	 */
	private Long idSeason;
	/**
	 * fecha y hora del partido
	 */
	private Date dateMatch;
	/**
	 * identificador del equipo local
	 */
	private Long idTeamA;
	/**
	 * nombre del equipo local
	 */
	private String teamAName;
	/**
	 * identificador del equipo visitante
	 */
	private Long idTeamB;
	/**
	 * nombre del equipo visitante
	 */
	private String teamBName;
	/**
	 * status codes: Playing - Match is still playing (live), Fixture - Match has not yet started, Postponed - Match is postponed (status will change to Fixture once its rescheduled), Suspended -
	 * Match is suspended during the match and can be resumed again, Played - Match has been played, Cancelled - Match is cancelled and wont be played again
	 */
	private String status;
	/**
	 * Contains the gameweek a match belongs to. Only available for domestic leagues
	 */
	private Long gameweek;
	/**
	 * Determines the winner of match: yet unknown, team_A, team_B, draw
	 */
	private String winner;
	/**
	 * Full-time score represents the final score, including any goals beyond normal time. local team
	 */
	private Long fsA;
	/**
	 * Full-time score represents the final score, including any goals beyond normal time. visitor team
	 */
	private Long fsB;
	/**
	 * Half-time score is available as soon as first half ends. Its a way to determine that half-time break has begun. local team
	 */
	private Long htsA;
	/**
	 * Half-time score is available as soon as first half ends. Its a way to determine that half-time break has begun. visitor team
	 */
	private Long htsB;
	/**
	 * Extra-time score, available when match gets into overtime. Since then fs_* fields are not updating. Set to fs_A as soon as match gets into overtime
	 */
	private Long etsA;
	/**
	 * Extra-time score, available when match gets into overtime. Since then fs_* fields are not updating. Set to fs_B as soon as match gets into overtime
	 */
	private Long etsB;
	/**
	 * Penalty shoot-out score (usually when match has ended with draw).
	 */
	private Long psA;
	/**
	 * Penalty shoot-out score (usually when match has ended with draw).
	 */
	private Long psB;
	/**
	 * When record was last updated.
	 */
	private Date lastUpdated;
	/**
	 * Shows actual minute of regular time when status=Playing. (Available in get_matches_live when using parameter minutes=yes)
	 */
	private Long minute;
	/**
	 * Shows actual minute of additional period time (often referred as 90+2) when status=Playing. (Available in get_matches_live when using parameter minutes=yes)
	 */
	private Long minuteExtra;
	/**
	 * Determines the stage of a match (Available in get_matches_live when using parameter minutes=yes) : 1H - First half, 2H - Second half, HT - Half time, E1 - Extra time, first half, E2 - Extra
	 * time, second half, EH - Extra time, break, PS - Penalty shootout, FT - Fulltime, - "Empty" option
	 */
	private String matchPeriod;

	public Long getIdMatch() {
		return idMatch;
	}

	public void setIdMatch(Long idMatch) {
		this.idMatch = idMatch;
	}

	public Long getIdMatchOpta() {
		return idMatchOpta;
	}

	public void setIdMatchOpta(Long idMatchOpta) {
		this.idMatchOpta = idMatchOpta;
	}

	public Long getIdSeason() {
		return idSeason;
	}

	public void setIdSeason(Long idSeason) {
		this.idSeason = idSeason;
	}

	public Date getDateMatch() {
		return dateMatch;
	}

	public void setDateMatch(Date dateMatch) {
		this.dateMatch = dateMatch;
	}

	public Long getIdTeamA() {
		return idTeamA;
	}

	public void setIdTeamA(Long idTeamA) {
		this.idTeamA = idTeamA;
	}

	public String getTeamAName() {
		return teamAName;
	}

	public void setTeamAName(String teamAName) {
		this.teamAName = teamAName;
	}

	public Long getIdTeamB() {
		return idTeamB;
	}

	public void setIdTeamB(Long idTeamB) {
		this.idTeamB = idTeamB;
	}

	public String getTeamBName() {
		return teamBName;
	}

	public void setTeamBName(String teamBName) {
		this.teamBName = teamBName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getGameweek() {
		return gameweek;
	}

	public void setGameweek(Long gameweek) {
		this.gameweek = gameweek;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public Long getFsA() {
		return fsA;
	}

	public void setFsA(Long fsA) {
		this.fsA = fsA;
	}

	public Long getFsB() {
		return fsB;
	}

	public void setFsB(Long fsB) {
		this.fsB = fsB;
	}

	public Long getHtsA() {
		return htsA;
	}

	public void setHtsA(Long htsA) {
		this.htsA = htsA;
	}

	public Long getHtsB() {
		return htsB;
	}

	public void setHtsB(Long htsB) {
		this.htsB = htsB;
	}

	public Long getEtsA() {
		return etsA;
	}

	public void setEtsA(Long etsA) {
		this.etsA = etsA;
	}

	public Long getEtsB() {
		return etsB;
	}

	public void setEtsB(Long etsB) {
		this.etsB = etsB;
	}

	public Long getPsA() {
		return psA;
	}

	public void setPsA(Long psA) {
		this.psA = psA;
	}

	public Long getPsB() {
		return psB;
	}

	public void setPsB(Long psB) {
		this.psB = psB;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Long getMinute() {
		return minute;
	}

	public void setMinute(Long minute) {
		this.minute = minute;
	}

	public Long getMinuteExtra() {
		return minuteExtra;
	}

	public void setMinuteExtra(Long minuteExtra) {
		this.minuteExtra = minuteExtra;
	}

	public String getMatchPeriod() {
		return matchPeriod;
	}

	public void setMatchPeriod(String matchPeriod) {
		this.matchPeriod = matchPeriod;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Match [idMatch=").append(idMatch).append(", idMatchOpta=").append(idMatchOpta).append(", idSeason=").append(idSeason).append(", dateMatch=").append(dateMatch)
				.append(", idTeamA=").append(idTeamA).append(", teamAName=").append(teamAName).append(", idTeamB=").append(idTeamB).append(", teamBName=").append(teamBName).append(", status=")
				.append(status).append("]");
		return builder.toString();
	}

}
