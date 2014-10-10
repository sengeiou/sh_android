package com.fav24.shootr.dao.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.fav24.shootr.dao.MatchDAO;
import com.fav24.shootr.dao.domain.Match;
import com.fav24.shootr.dao.utils.PropertiesManager;

public class MatchDAOImpl extends BaseDAOImpl implements MatchDAO {

	@Override
	public Match getMatchByID(Long idMatch) {
		String query = PropertiesManager.getProperty("match.select.one.byid");
		List<Match> list = getJdbcTemplate().query(query, new Object[] { idMatch }, new BeanPropertyRowMapper<Match>(Match.class));
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	@Override
	public Long insertMatch(Match match) {
		String query = PropertiesManager.getProperty("match.insert");

		PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query);
		factory.setGeneratedKeysColumnNames(new String[] { "idMatch" });
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idMatchOpta
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idSeason
		factory.addParameter(new SqlParameter(Types.TIME));// dateMatch
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idTeamA
		factory.addParameter(new SqlParameter(Types.VARCHAR));// teamAName
		factory.addParameter(new SqlParameter(Types.NUMERIC));// idTeamB
		factory.addParameter(new SqlParameter(Types.VARCHAR));// teamBName
		factory.addParameter(new SqlParameter(Types.VARCHAR));// status
		factory.addParameter(new SqlParameter(Types.NUMERIC));// gameweek
		factory.addParameter(new SqlParameter(Types.VARCHAR));// winner
		factory.addParameter(new SqlParameter(Types.NUMERIC));// fsA
		factory.addParameter(new SqlParameter(Types.NUMERIC));// fsB
		factory.addParameter(new SqlParameter(Types.NUMERIC));// htsA
		factory.addParameter(new SqlParameter(Types.NUMERIC));// htsB
		factory.addParameter(new SqlParameter(Types.NUMERIC));// etsA
		factory.addParameter(new SqlParameter(Types.NUMERIC));// etsB
		factory.addParameter(new SqlParameter(Types.NUMERIC));// psA
		factory.addParameter(new SqlParameter(Types.NUMERIC));// psB
		factory.addParameter(new SqlParameter(Types.TIME));// lastUpdated
		factory.addParameter(new SqlParameter(Types.NUMERIC));// minute
		factory.addParameter(new SqlParameter(Types.NUMERIC));// minuteExtra
		factory.addParameter(new SqlParameter(Types.VARCHAR));// matchPeriod

		Object[] params = new Object[] { match.getIdMatchOpta(), match.getIdSeason(), (match.getDateMatch() != null) ? new Date(match.getDateMatch().getTime()) : null, match.getIdTeamA(),
				match.getTeamAName(), match.getIdTeamB(), match.getTeamBName(), match.getStatus(), match.getGameweek(), match.getWinner(), match.getFsA(), match.getFsB(), match.getHtsA(),
				match.getHtsB(), match.getEtsA(), match.getEtsB(), match.getPsA(), match.getPsB(), (match.getLastUpdated() != null) ? new Date(match.getLastUpdated().getTime()) : null,
				match.getMinute(), match.getMinuteExtra(), match.getMatchPeriod() };

		PreparedStatementCreator psc = factory.newPreparedStatementCreator(params);
		GeneratedKeyHolder gkf = new GeneratedKeyHolder();

		int result = getJdbcTemplate().update(psc, gkf);
		return (result > 0) ? new Long(gkf.getKey().longValue()) : -1;
	}

	/** {@inheritDoc} */
	public int batchInsertMatch(final List<Match> matches) {
		String query = PropertiesManager.getProperty("match.insert");
		getJdbcTemplate().batchUpdate(query, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Match m = matches.get(i);
				ps.setObject(1, m.getIdMatchOpta(), Types.NUMERIC);
				ps.setObject(2, m.getIdSeason(), Types.VARCHAR);
				ps.setObject(3, m.getDateMatch()!= null ? new Timestamp(m.getDateMatch().getTime()) : null, Types.TIMESTAMP);
				ps.setObject(4, m.getIdTeamA(), Types.NUMERIC);
				ps.setObject(5, m.getTeamAName(), Types.VARCHAR);
				ps.setObject(6, m.getIdTeamB(), Types.NUMERIC);
				ps.setObject(7, m.getTeamBName(), Types.VARCHAR);
				ps.setObject(8, m.getStatus(), Types.VARCHAR);
				ps.setObject(9, m.getGameweek(), Types.NUMERIC);
				ps.setObject(10, m.getWinner(), Types.VARCHAR);
				ps.setObject(11, m.getFsA(), Types.NUMERIC);
				ps.setObject(12, m.getFsB(), Types.NUMERIC);
				ps.setObject(13, m.getHtsA(), Types.NUMERIC);
				ps.setObject(14, m.getHtsB(), Types.NUMERIC);
				ps.setObject(15, m.getEtsA(), Types.NUMERIC);
				ps.setObject(16, m.getEtsB(), Types.NUMERIC);
				ps.setObject(17, m.getPsA(), Types.NUMERIC);
				ps.setObject(18, m.getPsB(), Types.NUMERIC);
				ps.setObject(19, m.getLastUpdated()!= null ? new Timestamp(m.getLastUpdated().getTime()) : null, Types.TIMESTAMP);
				ps.setObject(20, m.getMinute(), Types.NUMERIC);
				ps.setObject(21, m.getMinuteExtra(), Types.NUMERIC);
				ps.setObject(22, m.getMatchPeriod(), Types.VARCHAR);
			}

			public int getBatchSize() {
				return matches.size();
			}
		});
		// TODO no retornar fakes
		return matches.size();
	}

	@Override
	public long updateMatch(Match m) {
		String query = PropertiesManager.getProperty("match.update");
		return getJdbcTemplate().update(query, m.getDateMatch(), m.getIdTeamA(), m.getTeamAName(), m.getIdTeamB(), m.getTeamBName(), m.getStatus(), m.getGameweek(), m.getWinner(), m.getFsA(),
				m.getFsB(), m.getHtsA(), m.getHtsB(), m.getEtsA(), m.getEtsB(), m.getPsA(), m.getPsB(), m.getLastUpdated(), m.getMinute(), m.getMinuteExtra(), m.getMatchPeriod(), m.getIdMatch());
	}

	/** {@inheritDoc} */
	public int batchUpdateMatch(final List<Match> matches) {
		String query = PropertiesManager.getProperty("match.update");
		getJdbcTemplate().batchUpdate(query, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Match m = matches.get(i);
				ps.setObject(1, (m.getDateMatch() != null) ? new Date(m.getDateMatch().getTime()) : null, Types.TIME);
				ps.setObject(2, m.getIdTeamA(), Types.NUMERIC);
				ps.setObject(3, m.getTeamAName(), Types.VARCHAR);
				ps.setObject(4, m.getIdTeamB(), Types.NUMERIC);
				ps.setObject(5, m.getTeamBName(), Types.VARCHAR);
				ps.setObject(6, m.getStatus(), Types.VARCHAR);
				ps.setObject(7, m.getGameweek(), Types.NUMERIC);
				ps.setObject(8, m.getWinner(), Types.VARCHAR);
				ps.setObject(9, m.getFsA(), Types.NUMERIC);
				ps.setObject(10, m.getFsB(), Types.NUMERIC);
				ps.setObject(11, m.getHtsA(), Types.NUMERIC);
				ps.setObject(12, m.getHtsB(), Types.NUMERIC);
				ps.setObject(13, m.getEtsA(), Types.NUMERIC);
				ps.setObject(14, m.getEtsB(), Types.NUMERIC);
				ps.setObject(15, m.getPsA(), Types.NUMERIC);
				ps.setObject(16, m.getPsB(), Types.NUMERIC);
				ps.setObject(17, m.getLastUpdated()!= null ? new Timestamp(m.getLastUpdated().getTime()) : null, Types.TIMESTAMP);
				ps.setObject(18, m.getMinute(), Types.NUMERIC);
				ps.setObject(19, m.getMinuteExtra(), Types.NUMERIC);
				ps.setObject(20, m.getMatchPeriod(), Types.VARCHAR);
				ps.setObject(21, m.getIdMatch(), Types.NUMERIC);
			}

			public int getBatchSize() {
				return matches.size();
			}
		});
		// TODO no retornar fakes
		return matches.size();
	}

	@Override
	public List<Match> getAllMatchesByAreaId(Long idArea) {
		String query = PropertiesManager.getProperty("match.select.byidArea");
		return getJdbcTemplate().query(query, new Object[] { idArea }, new BeanPropertyRowMapper<Match>(Match.class));
	}

	@Override
	public List<Match> getAllMatchesBySeasonId(Long idSeason) {
		String query = PropertiesManager.getProperty("match.select.byidSeason");
		return getJdbcTemplate().query(query, new Object[] { idSeason }, new BeanPropertyRowMapper<Match>(Match.class));
	}

	@Override
	public List<Match> getAllMatchesBySeasonOptaId(Long idSeasonOpta) {
		String query = PropertiesManager.getProperty("match.select.byidSeasonOpta");
		return getJdbcTemplate().query(query, new Object[] { idSeasonOpta }, new BeanPropertyRowMapper<Match>(Match.class));
	}

}
