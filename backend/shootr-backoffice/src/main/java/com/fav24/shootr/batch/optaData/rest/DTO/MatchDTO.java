package com.fav24.shootr.batch.optaData.rest.DTO;

public class MatchDTO {

	/*
	 * <match match_id="1713095" ow_match_id="758638" date_utc="2014-08-22" time_utc="18:30:00" date_london="2014-08-22" time_london="19:30:00" team_A_id="961" ow_team_A_id="156"
	 * team_A_name="Bayern München" team_A_country="DEU" team_B_id="968" ow_team_B_id="172" team_B_name="Wolfsburg" team_B_country="DEU" status="Played" gameweek="1" winner="team_A" fs_A="2" fs_B="1"
	 * hts_A="1" hts_B="0" ets_A="" ets_B="" ps_A="" ps_B="" last_updated="2014-09-16 08:36:52"/>
	 */

	/**
	 * Unique identifier of match.
	 */
	private Long match_id;
	/**
	 * OPTA match ID
	 */
	private Long ow_match_id;

	/**
	 * Date of match (UTC), format 'yyyy-mm-dd'.
	 */
	private String date_utc;
	/**
	 * Start time (UTC), format 'hh:mm:ss'.
	 */
	private String time_utc;

	/**
	 * Unique team ID for the home team.
	 */
	private Long team_A_id;
	/**
	 * OPTA team ID for the home team
	 */
	private Long ow_team_A_id;

	/**
	 * Name of the home team.
	 */
	private String team_A_name;

	/**
	 * Unique team ID for the away team.
	 */
	private Long team_B_id;
	/**
	 * OPTA team ID for the away team
	 */
	private Long ow_team_B_id;
	/**
	 * Name of the away team.
	 */
	private String team_B_name;

	/**
	 * We currently use the following match status codes: Playing - Match is still playing (live) Fixture - Match has not yet started Postponed - Match is postponed (status will change to Fixture once
	 * it's rescheduled) Suspended - Match is suspended during the match and can be resumed again Played - Match has been played Cancelled - Match is cancelled and won't be played again
	 */
	private String status;
	/**
	 * Contains the gameweek a match belongs to. Only available for domestic leagues.
	 */
	private Long gameweek;

	/**
	 * Determines the winner of match: yet unknown team_A team_B draw
	 */
	private String winner;

	/**
	 * Full-time score represents the final score, including any goals beyond normal time.
	 */
	private Long fs_A;
	/**
	 * Full-time score represents the final score, including any goals beyond normal time.
	 */
	private Long fs_B;

	/**
	 * Half-time score is available as soon as first half ends. It's a way to determine that half-time break has begun.
	 */
	private Long hts_A;
	/**
	 * Half-time score is available as soon as first half ends. It's a way to determine that half-time break has begun.
	 */
	private Long hts_B;

	/**
	 * extra-time score, available when match gets into overtime. Since then fs_* fields are not updating. Set to fs_A as soon as match gets into overtime.
	 */
	private Long ets_A;
	/**
	 * xtra-time score, available when match gets into overtime. Since then fs_* fields are not updating. Set to fs_A as soon as match gets into overtime.
	 */
	private Long ets_B;

	/**
	 * Penalty shoot-out score (usually when match has ended with draw).
	 */
	private Long ps_A;
	/**
	 * Penalty shoot-out score (usually when match has ended with draw).
	 */
	private Long ps_B;

	/**
	 * When record was last updated. Format YYYY-MM-DD HH:MM:SS. CET timezone.
	 */
	private String last_updated;

	/* Live Only */

	/**
	 * Shows actual minute of regular time when status=Playing. (Available in get_matches_live when using parameter minutes=yes)
	 */
	private Long minute;
	/**
	 * Shows actual minute of additional period time (often referred as 90'+2') when status=Playing. (Available in get_matches_live when using parameter minutes=yes)
	 */
	private Long minute_extra;
	/**
	 * Determines the stage of a match (Available in get_matches_live when using parameter minutes=yes) : 1H - First half 2H - Second half HT - Half time E1 - Extra time, first half E2 - Extra time,
	 * second half EH - Extra time, break PS - Penalty shootout FT - Fulltime - "Empty" option
	 */
	private String match_period;

	public MatchDTO() {
	}

	public MatchDTO(Long match_id, Long ow_match_id, String date_utc, String time_utc, Long team_A_id, Long ow_team_A_id, String team_A_name, Long team_B_id, Long ow_team_B_id, String team_B_name,
			String status, Long gameweek, String winner, Long fs_A, Long fs_B, Long hts_A, Long hts_B, Long ets_A, Long ets_B, Long ps_A, Long ps_B, String last_updated) {
		this.match_id = match_id;
		this.ow_match_id = ow_match_id;
		this.date_utc = date_utc;
		this.time_utc = time_utc;
		this.team_A_id = team_A_id;
		this.ow_team_A_id = ow_team_A_id;
		this.team_A_name = team_A_name;
		this.team_B_id = team_B_id;
		this.ow_team_B_id = ow_team_B_id;
		this.team_B_name = team_B_name;
		this.status = status;
		this.gameweek = gameweek;
		this.winner = winner;
		this.fs_A = fs_A;
		this.fs_B = fs_B;
		this.hts_A = hts_A;
		this.hts_B = hts_B;
		this.ets_A = ets_A;
		this.ets_B = ets_B;
		this.ps_A = ps_A;
		this.ps_B = ps_B;
		this.last_updated = last_updated;
	}

	public MatchDTO(Long match_id, Long ow_match_id, String date_utc, String time_utc, Long team_A_id, Long ow_team_A_id, String team_A_name, Long team_B_id, Long ow_team_B_id, String team_B_name,
			String status, Long gameweek, String winner, Long fs_A, Long fs_B, Long hts_A, Long hts_B, Long ets_A, Long ets_B, Long ps_A, Long ps_B, String last_updated, Long minute,
			Long minute_extra, String match_period) {
		this.match_id = match_id;
		this.ow_match_id = ow_match_id;
		this.date_utc = date_utc;
		this.time_utc = time_utc;
		this.team_A_id = team_A_id;
		this.ow_team_A_id = ow_team_A_id;
		this.team_A_name = team_A_name;
		this.team_B_id = team_B_id;
		this.ow_team_B_id = ow_team_B_id;
		this.team_B_name = team_B_name;
		this.status = status;
		this.gameweek = gameweek;
		this.winner = winner;
		this.fs_A = fs_A;
		this.fs_B = fs_B;
		this.hts_A = hts_A;
		this.hts_B = hts_B;
		this.ets_A = ets_A;
		this.ets_B = ets_B;
		this.ps_A = ps_A;
		this.ps_B = ps_B;
		this.last_updated = last_updated;
		this.minute = minute;
		this.minute_extra = minute_extra;
		this.match_period = match_period;
	}

	public Long getMatch_id() {
		return match_id;
	}

	public void setMatch_id(Long match_id) {
		this.match_id = match_id;
	}

	public Long getOw_match_id() {
		return ow_match_id;
	}

	public void setOw_match_id(Long ow_match_id) {
		this.ow_match_id = ow_match_id;
	}

	public String getDate_utc() {
		return date_utc;
	}

	public void setDate_utc(String date_utc) {
		this.date_utc = date_utc;
	}

	public String getTime_utc() {
		return time_utc;
	}

	public void setTime_utc(String time_utc) {
		this.time_utc = time_utc;
	}

	public Long getTeam_A_id() {
		return team_A_id;
	}

	public void setTeam_A_id(Long team_A_id) {
		this.team_A_id = team_A_id;
	}

	public Long getOw_team_A_id() {
		return ow_team_A_id;
	}

	public void setOw_team_A_id(Long ow_team_A_id) {
		this.ow_team_A_id = ow_team_A_id;
	}

	public String getTeam_A_name() {
		return team_A_name;
	}

	public void setTeam_A_name(String team_A_name) {
		this.team_A_name = team_A_name;
	}

	public Long getTeam_B_id() {
		return team_B_id;
	}

	public void setTeam_B_id(Long team_B_id) {
		this.team_B_id = team_B_id;
	}

	public Long getOw_team_B_id() {
		return ow_team_B_id;
	}

	public void setOw_team_B_id(Long ow_team_B_id) {
		this.ow_team_B_id = ow_team_B_id;
	}

	public String getTeam_B_name() {
		return team_B_name;
	}

	public void setTeam_B_name(String team_B_name) {
		this.team_B_name = team_B_name;
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

	public Long getFs_A() {
		return fs_A;
	}

	public void setFs_A(Long fs_A) {
		this.fs_A = fs_A;
	}

	public Long getFs_B() {
		return fs_B;
	}

	public void setFs_B(Long fs_B) {
		this.fs_B = fs_B;
	}

	public Long getHts_A() {
		return hts_A;
	}

	public void setHts_A(Long hts_A) {
		this.hts_A = hts_A;
	}

	public Long getHts_B() {
		return hts_B;
	}

	public void setHts_B(Long hts_B) {
		this.hts_B = hts_B;
	}

	public Long getEts_A() {
		return ets_A;
	}

	public void setEts_A(Long ets_A) {
		this.ets_A = ets_A;
	}

	public Long getEts_B() {
		return ets_B;
	}

	public void setEts_B(Long ets_B) {
		this.ets_B = ets_B;
	}

	public Long getPs_A() {
		return ps_A;
	}

	public void setPs_A(Long ps_A) {
		this.ps_A = ps_A;
	}

	public Long getPs_B() {
		return ps_B;
	}

	public void setPs_B(Long ps_B) {
		this.ps_B = ps_B;
	}

	public String getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(String last_updated) {
		this.last_updated = last_updated;
	}

	public Long getMinute() {
		return minute;
	}

	public void setMinute(Long minute) {
		this.minute = minute;
	}

	public Long getMinute_extra() {
		return minute_extra;
	}

	public void setMinute_extra(Long minute_extra) {
		this.minute_extra = minute_extra;
	}

	public String getMatch_period() {
		return match_period;
	}

	public void setMatch_period(String match_period) {
		this.match_period = match_period;
	}

	@Override
	public String toString() {
		return "MatchDTO{" + "ow_match_id=" + ow_match_id + ", match_id=" + match_id + ", team_A_name='" + team_A_name + '\'' + ", team_B_name='" + team_B_name + '\'' + ", status='" + status + '\''
				+ ", gameweek=" + gameweek + ", winner='" + winner + '\'' + ", fs_A=" + fs_A + ", fs_B=" + fs_B + '}';
	}
}
