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

import com.fav24.shootr.dao.SeasonDAO;
import com.fav24.shootr.dao.domain.Season;
import com.fav24.shootr.dao.utils.PropertiesManager;

public class SeasonDAOImpl extends BaseDAOImpl implements SeasonDAO {

	/** {@inheritDoc} */
	public List<Season> getAllSeasons() {
		String query = PropertiesManager.getProperty("season.select.all");
		return getJdbcTemplate().query(query, new Object[] {}, new BeanPropertyRowMapper<Season>(Season.class));
	}

	/** {@inheritDoc} */
	public Season getSeasonByID(Long idSeason) {
		String query = PropertiesManager.getProperty("season.select.one.byid");
		List<Season> list = getJdbcTemplate().query(query, new Object[] { idSeason }, new BeanPropertyRowMapper<Season>(Season.class));
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}
	
	/** {@inheritDoc} */
	public Season getSeasonByOptaId(Long idSeasonOpta) {
		String query = PropertiesManager.getProperty("season.select.one.byidOpta");
		List<Season> list = getJdbcTemplate().query(query, new Object[] { idSeasonOpta }, new BeanPropertyRowMapper<Season>(Season.class));
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	/** {@inheritDoc} */
	public Long insertSeason(Season season) {
		// INSERT INTO `Season` (`idSeasonOpta`, `idCompetition`, `name`, `startDate`, `endDate`, `lastUpdate`, `csys_birth`, `csys_modified`, `csys_revision`) VALUES (?, ?, ?, ?, ?, ?, now(), now(), 0)


		String query = PropertiesManager.getProperty("season.insert");
		PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query);
		factory.setGeneratedKeysColumnNames(new String[] { "idSeason" });
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idSeasonOpta
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idCompetition
		factory.addParameter(new SqlParameter(Types.VARCHAR));// name
		factory.addParameter(new SqlParameter(Types.DATE));// startDate
		factory.addParameter(new SqlParameter(Types.DATE));// endDate
		factory.addParameter(new SqlParameter(Types.DATE));// lastUpdate

		Object[] params = new Object[] { season.getIdSeasonOpta(), season.getIdCompetition(), season.getName(), season.getStartDate(), season.getEndDate(), season.getLastUpdated() };

		PreparedStatementCreator psc = factory.newPreparedStatementCreator(params);
		GeneratedKeyHolder gkf = new GeneratedKeyHolder();

		int result = getJdbcTemplate().update(psc, gkf);
		return (result > 0) ? new Long(gkf.getKey().longValue()) : -1;
	}

	/** {@inheritDoc} */
	public int batchInsertSeason(final List<Season> seasons) {
		String query = PropertiesManager.getProperty("season.insert");
		getJdbcTemplate().batchUpdate(query, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Season b = seasons.get(i);
				ps.setLong(1, b.getIdSeasonOpta());
				ps.setLong(2, b.getIdCompetition());
				ps.setString(3, b.getName());
				ps.setDate(4, new java.sql.Date(b.getStartDate().getTime()));
				ps.setDate(5, new java.sql.Date(b.getEndDate().getTime()));
				ps.setDate(6, new java.sql.Date(b.getLastUpdated().getTime()));
			}
			public int getBatchSize() {
				return seasons.size();
			}
		});
		//TODO no retornar fakes
		return seasons.size();
	}
	
	@Override
	public List<Season> getAllSeasonsByCompetitionId(Long idCompetition) {
		String query = PropertiesManager.getProperty("season.select.byidCompetition");
		return getJdbcTemplate().query(query, new Object[] { idCompetition }, new BeanPropertyRowMapper<Season>(Season.class));
	}

	@Override
	public int updateSeason(Season season) {
		String query = PropertiesManager.getProperty("season.update");
		return getJdbcTemplate().update(query, season.getName(), season.getStartDate(), season.getEndDate(), season.getLastUpdated(), season.getIdSeason());
	}

}
