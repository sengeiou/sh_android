package com.fav24.shootr.dao;

import com.fav24.shootr.dao.domain.Competition;

import java.util.List;


public interface CompetitionDAO extends BaseDAO {

    /**
     * Returns all Competitions of the system
     * @return all Competitions of the system
     */
    List<Competition> getAllCompetitions();

    /**
     * Given an idCompetition, returns the Competition that match with idCompetition
     * @param idCompetition Competition identifier
     * @return Competition that match with given idCompetition
     */
    Competition getCompetitionByID(Long idCompetition);

    /**
     * Given an idCompetitionOpta, returns the Competition that match with idCompetitionOpta
     * @param idCompetitionOpta Competition identifier opta
     * @return Competition that match with given idCompetitionOpta
     */
    Competition getCompetitionByOptaId(Long idCompetitionOpta);
    
    /**
     * Given an Competition Object, this method insert it into DataBase and returns the generated idCompetition
     * @param Competition object to insert
     * @return generated idCompetition of the inserted Competition
     */
    Long insertCompetition(Competition competition);
    
    /**
     * Insert multiple Competitions.
     * @param Competitions
     * @return
     */
    int batchInsertCompetition(final List<Competition> competitions);

    /**
     * Given an idArea, return all its Competitions.
     * @param idArea area identifier.
     * @return Competitions of the area.
     */
	List<Competition> getAllCompetitionsByAreaId(Long idArea);
}
