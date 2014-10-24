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

import com.fav24.shootr.dao.CompetitionDAO;
import com.fav24.shootr.dao.domain.Competition;
import com.fav24.shootr.dao.utils.PropertiesManager;

public class CompetitionDAOImpl extends BaseDAOImpl implements CompetitionDAO {

	/** {@inheritDoc} */
	public List<Competition> getAllCompetitions() {
		String query = PropertiesManager.getProperty("competition.select.all");
		return getJdbcTemplate().query(query, new Object[] {}, new BeanPropertyRowMapper<Competition>(Competition.class));
	}

	/** {@inheritDoc} */
	public Competition getCompetitionByID(Long idCompetition) {
		String query = PropertiesManager.getProperty("competition.select.one.byid");
		List<Competition> list = getJdbcTemplate().query(query, new Object[] { idCompetition }, new BeanPropertyRowMapper<Competition>(Competition.class));
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}
	
	/** {@inheritDoc} */
	public Competition getCompetitionByOptaId(Long idCompetitionOpta) {
		String query = PropertiesManager.getProperty("competition.select.one.byidOpta");
		List<Competition> list = getJdbcTemplate().query(query, new Object[] { idCompetitionOpta }, new BeanPropertyRowMapper<Competition>(Competition.class));
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	/** {@inheritDoc} */
	public Long insertCompetition(Competition competition) {
		//INSERT INTO `Competition` (`idArea`, `idCompetitionOpta`, `name`, `format`, `displayOrder`, `type`, `csys_birth`, `csys_modified`, `csys_revision`) VALUES (?, ?, ?, ?, ?, ?, now(), now(), 0)

		String query = PropertiesManager.getProperty("competition.insert");
		PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query);
		factory.setGeneratedKeysColumnNames(new String[] { "idCompetition" });
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idArea
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idCompetitionOpta
		factory.addParameter(new SqlParameter(Types.VARCHAR));// name
		factory.addParameter(new SqlParameter(Types.VARCHAR));// format
		factory.addParameter(new SqlParameter(Types.NUMERIC));// displayOrder
		factory.addParameter(new SqlParameter(Types.VARCHAR));// type

		Object[] params = new Object[] { competition.getIdArea(), competition.getIdCompetitionOpta(), competition.getName(), competition.getFormat(), competition.getDisplayOrder(), competition.getType()};

		PreparedStatementCreator psc = factory.newPreparedStatementCreator(params);
		GeneratedKeyHolder gkf = new GeneratedKeyHolder();

		int result = getJdbcTemplate().update(psc, gkf);
		return (result > 0) ? new Long(gkf.getKey().longValue()) : -1;
	}

	/** {@inheritDoc} */
	public int batchInsertCompetition(final List<Competition> Competitions) {
		String query = PropertiesManager.getProperty("competition.insert");
		getJdbcTemplate().batchUpdate(query, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Competition b = Competitions.get(i);
				//idMatchBetType, name, description, oddValue, url, providerIdBetTypeOdd
				ps.setLong(1, b.getIdArea());
				ps.setLong(2, b.getIdCompetitionOpta());
				ps.setString(3, b.getName());
				ps.setString(4, b.getFormat());
				ps.setLong(5, b.getDisplayOrder());
				ps.setString(6, b.getType());
			}
			public int getBatchSize() {
				return Competitions.size();
			}
		});
		//TODO no retornar fakes
		return Competitions.size();
	}
	
	/** {@inheritDoc} */
	public List<Competition> getAllCompetitionsByAreaId(Long idArea) {
		String query = PropertiesManager.getProperty("competition.select.byidArea");
		return getJdbcTemplate().query(query, new Object[] { idArea }, new BeanPropertyRowMapper<Competition>(Competition.class));
	}

}
