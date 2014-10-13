package com.fav24.shootr.dao;

import com.fav24.shootr.dao.domain.Season;

import java.util.List;


public interface SeasonDAO extends BaseDAO {

    /**
     * Returns all Seasons of the system
     * @return all Seasons of the system
     */
    List<Season> getAllSeasons();

    /**
     * Given an idSeason, returns the Season that match with idSeason
     * @param idSeason Season identifier
     * @return Season that match with given idSeason
     */
    Season getSeasonByID(Long idSeason);

    /**
     * Given an idSeasonOpta, returns the Season that match with idSeasonOpta
     * @param idSeasonOpta Season identifier opta
     * @return Season that match with given idSeasonOpta
     */
    Season getSeasonByOptaId(Long idSeasonOpta);
    
    /**
     * Given an Season Object, this method insert it into DataBase and returns the generated idSeason
     * @param Season object to insert
     * @return generated idSeason of the inserted Season
     */
    Long insertSeason(Season season);
    
    /**
     * Insert multiple Seasons.
     * @param Seasons
     * @return
     */
    int batchInsertSeason(final List<Season> seasons);

    /**
     * Given an idCompetition, return all its Seasons.
     * @param idCompetition competition identifier.
     * @return Seasons of the competition.
     */
	List<Season> getAllSeasonsByCompetitionId(Long idCompetition);


    /**
     * Given an idArea, return all its Seasons.
     * @param idArea area identifier.
     * @return Seasons of the area.
     */
	List<Season> getAllSeasonsByAreaId(Long idArea);
	
	/**
	 * Update season.
	 * @param season
	 * @return
	 */
	int updateSeason(Season season);
	
	/**
	 * Update multiple seasons
	 * @param seasons
	 * @return
	 */
	int batchUpdateSeason(List<Season> seasons);
}
