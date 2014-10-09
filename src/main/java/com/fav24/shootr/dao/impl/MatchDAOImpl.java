package com.fav24.shootr.dao.impl;

import java.sql.Date;
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
//		INSERT INTO `Matches` (`idMatchOpta`, `idSeason`, `dateMatch`, `idTeamA`, `teamAName`, `idTeamB`, `teamBName`, `status`, `gameweek`, `winner`, `fsA`, `fsB`, `htsA`, `htsB`, `etsA`, `etsB`, `psA`, `psB`, `lastUpdated`, `minute`, `minuteExtra`, `matchPeriod`, `csys_birth`, `csys_modified`, `csys_revision`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now(), 0)

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
		
		Object[] params = new Object[] { 
				match.getIdMatchOpta(), 
				match.getIdSeason(),
				new Date(match.getDateMatch().getTime()),
				match.getIdTeamA(),
				match.getTeamAName(),
				match.getIdTeamB(),
				match.getTeamBName(),
				match.getStatus(),
				match.getGameweek(),
				match.getWinner(),
				match.getFsA(),
				match.getFsB(),
				match.getHtsA(),
				match.getHtsB(),
				match.getEtsA(),
				match.getEtsB(),
				match.getPsA(),
				match.getPsB(),
				new Date(match.getLastUpdated().getTime()),
				match.getMinute(),
				match.getMinuteExtra(),
				match.getMatchPeriod()
			};

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
				ps.setLong(1, m.getIdMatchOpta());
				ps.setLong(2, m.getIdSeason());
				ps.setDate(3, new Date(m.getDateMatch().getTime()));
				ps.setLong(4,  m.getIdTeamA());
				ps.setString(5, m.getTeamAName());
				ps.setLong(6,  m.getIdTeamB());
				ps.setString(7, m.getTeamBName());
				ps.setString(8, m.getStatus());
				ps.setLong(9,  m.getGameweek());
				ps.setString(10, m.getWinner());
				ps.setLong(11,  m.getFsA());
				ps.setLong(12,  m.getFsB());
				ps.setLong(13,  m.getHtsA());
				ps.setLong(14,  m.getHtsB());
				ps.setLong(15,  m.getEtsA());
				ps.setLong(16,  m.getEtsB());
				ps.setLong(17,  m.getPsA());
				ps.setLong(18,  m.getPsB());
				ps.setDate(19, new Date(m.getLastUpdated().getTime()));
				ps.setLong(20,  m.getMinute());
				ps.setLong(21,  m.getMinuteExtra());
				ps.setString(22, m.getMatchPeriod());
			}
			public int getBatchSize() {
				return matches.size();
			}
		});
		//TODO no retornar fakes
		return matches.size();
	}
	
	@Override
	public long updateMatch(Match m) {
		//UPDATE `Matches` SET `dateMatch` = ?, `idTeamA` = ?, `teamAName` = ?, `idTeamB` = ?, `teamBName` = ?, `status` = ?, `gameweek` = ?, `winner` = ?, `fsA` = ?, `fsB` = ?
		//, `htsA` = ?, `htsB` = ?, `etsA` = ?, `etsB` = ?, `psA` = ?, `psB` = ?, `lastUpdated` = ?, `minute` = ?, `minuteExtra` = ?, `matchPeriod` = ?, `csys_modified` = now(), `csys_revision` = `csys_revision` = `csys_revision` + 1 WHERE `idMatch` = ?
		String query = PropertiesManager.getProperty("match.update");
		return getJdbcTemplate().update(query, m.getDateMatch(), m.getIdTeamA(), m.getTeamAName(),  m.getIdTeamB(), m.getTeamBName(), 
				m.getStatus(), m.getGameweek(), m.getWinner(), m.getFsA(), m.getFsB(), m.getHtsA(), m.getHtsB(), m.getEtsA(), m.getEtsB(), m.getPsA(), m.getPsB(),
				m.getLastUpdated(), m.getMinute(), m.getMinuteExtra(), m.getMatchPeriod(), m.getIdMatch());
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

}
