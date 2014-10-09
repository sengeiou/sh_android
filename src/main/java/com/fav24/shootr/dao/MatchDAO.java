package com.fav24.shootr.dao;

import com.fav24.shootr.dao.domain.Match;

import java.util.List;


public interface MatchDAO extends BaseDAO {

    /**
     * Given an idMatch, returns the Match that match with idMatch
     * @param idMatch Match identifier
     * @return Match that match with given idMatch
     */
    Match getMatchByID(Long idMatch);

    /**
     * Given an Match Object, this method insert it into DataBase and returns the generated idMatch
     * @param Match object to insert
     * @return generated idMatch of the inserted Match
     */
    Long insertMatch(Match Match);
    
    /**
     * Insert multiple Matchs.
     * @param Matchs
     * @return
     */
    int batchInsertMatch(final List<Match> Matches);

    /**
     * Given an Match Object, this method update it into DataBase and returns the number of the updated rows
     * @param Match object to update
     * @return number of the updated rows
     */
    long updateMatch(Match Match);

    /**
     * Given an idArea, return all its Matchs.
     * @param idArea area identifier.
     * @return Matchs of the area.
     */
	List<Match> getAllMatchesByAreaId(Long idArea);
	
	 /**
     * Given an idSeason, return all its Matchs.
     * @param idSeason season identifier.
     * @return Matchs of the season.
     */
	List<Match> getAllMatchesBySeasonId(Long idSeason);
	
	/**
     * Given an idSeasonOpta, return all its Matchs.
     * @param idSeasonOpta opta season identifier.
     * @return Matchs of the opta season.
     */
	List<Match> getAllMatchesBySeasonOptaId(Long idSeasonOpta);
}
