package com.fav24.shootr.batch.optaData.rest.DTO;


import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase representa los emparejamientos en rondas eliminatorias : Ej Cuartos de champions
 */
public class AggrDTO {

    private String winner;
    private Long winner_team_id;
    private Long ow_winner_team_id; //no
    private List<MatchDTO> matches;

    public AggrDTO(String winner, Long winner_team_id, Long ow_winner_team_id) {
        this.winner = winner;
        this.winner_team_id = winner_team_id;
        this.ow_winner_team_id = ow_winner_team_id;
        this.matches = new ArrayList<MatchDTO>();
    }

    public AggrDTO() {
        this.matches = new ArrayList<MatchDTO>();
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Long getWinner_team_id() {
        return winner_team_id;
    }

    public void setWinner_team_id(Long winner_team_id) {
        this.winner_team_id = winner_team_id;
    }

    public Long getOw_winner_team_id() {
        return ow_winner_team_id;
    }

    public void setOw_winner_team_id(Long ow_winner_team_id) {
        this.ow_winner_team_id = ow_winner_team_id;
    }

    public List<MatchDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchDTO> matches) {
        this.matches = matches;
    }

    @Override
    public String toString() {
        return "AggrDTO{" +
                "winner='" + winner + '\'' +
                ", winner_team_id=" + winner_team_id +
                ", ow_winner_team_id=" + ow_winner_team_id +
                '}';
    }


}
