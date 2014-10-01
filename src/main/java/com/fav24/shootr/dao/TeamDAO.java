package com.fav24.shootr.dao;

import com.fav24.shootr.dao.domain.Team;

import java.util.List;


public interface TeamDAO extends BaseDAO {

    /**
     * Returns all Teams of the system
     * @return all Teams of the system
     */
    List<Team> getAllTeams();

    /**
     * Given an idTeam, returns the team that match with idTeam
     * @param idTeam Team identifier
     * @return Team that match with given idTeam
     */
    Team getTeamByID(Long idTeam);

    /**
     * Given an Team Object, this method insert it into DataBase and returns the generated idTeam
     * @param team object to insert
     * @return generated idTeam of the inserted team
     */
    Long insertTeam(Team team);

    /**
     * Given an Team Object, this method update it into DataBase and returns the number of the updated rows
     * @param team object to update
     * @return number of the updated rows
     */
    long updateTeam(Team team);
}
