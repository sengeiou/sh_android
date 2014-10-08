package com.fav24.shootr.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.fav24.shootr.dao.TeamDAO;
import com.fav24.shootr.dao.domain.Team;
import com.fav24.shootr.dao.utils.PropertiesManager;

public class TeamDAOImpl extends BaseDAOImpl implements TeamDAO {

	@Override
	public List<Team> getAllTeams() {
		String query = PropertiesManager.getProperty("team.select.all");
		return getJdbcTemplate().query(query, new Object[] {}, new BeanPropertyRowMapper<Team>(Team.class));
	}

	@Override
	public Team getTeamByID(Long idTeam) {
		String query = PropertiesManager.getProperty("team.select.one.byid");
		List<Team> list = getJdbcTemplate().query(query, new Object[] { idTeam }, new BeanPropertyRowMapper<Team>(Team.class));
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	@Override
	public Long insertTeam(Team team) {
		String query = PropertiesManager.getProperty("team.insert");
		PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query);
		factory.setGeneratedKeysColumnNames(new String[] { "idTeam" });
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idTeamOpta
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idArea
		factory.addParameter(new SqlParameter(Types.VARCHAR));// clubname
		factory.addParameter(new SqlParameter(Types.VARCHAR));// officialName
		factory.addParameter(new SqlParameter(Types.VARCHAR));// shortName
		factory.addParameter(new SqlParameter(Types.VARCHAR));// tlatName

		Object[] params = new Object[] {team.getIdTeamOpta(), team.getIdArea(), team.getClubName(), team.getOfficialName(), team.getShortName(), team.getTlaName()};

		PreparedStatementCreator psc = factory.newPreparedStatementCreator(params);
		GeneratedKeyHolder gkf = new GeneratedKeyHolder();

		int result = getJdbcTemplate().update(psc, gkf);
		return (result > 0) ? new Long(gkf.getKey().longValue()) : -1;
	}

	/** {@inheritDoc} */
	public int batchInsertTeam(final List<Team> teams) {
		String query = PropertiesManager.getProperty("team.insert");
		getJdbcTemplate().batchUpdate(query, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Team b = teams.get(i);
				//idMatchBetType, name, description, oddValue, url, providerIdBetTypeOdd
				ps.setLong(1, b.getIdTeamOpta());
				ps.setLong(2, b.getIdArea());
				ps.setString(3, b.getClubName());
				ps.setString(4, b.getOfficialName());
				ps.setString(5, b.getShortName());
				ps.setString(6, b.getTlaName());
			}
			public int getBatchSize() {
				return teams.size();
			}
		});
		//TODO no retornar fakes
		return teams.size();
	}
	
	@Override
	public long updateTeam(Team team) {
		String query = PropertiesManager.getProperty("team.update");
		return getJdbcTemplate().update(query, team.getClubName(), team.getOfficialName(), team.getShortName(), team.getTlaName(), team.getIdTeam());
	}

	@Override
	public List<Team> getAllTeamsByAreaId(Long idArea) {
		String query = PropertiesManager.getProperty("team.select.byidArea");
		return getJdbcTemplate().query(query, new Object[] { idArea }, new BeanPropertyRowMapper<Team>(Team.class));
	}

}
