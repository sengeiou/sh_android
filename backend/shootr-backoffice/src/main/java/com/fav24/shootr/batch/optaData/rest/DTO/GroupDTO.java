package com.fav24.shootr.batch.optaData.rest.DTO;


import java.util.ArrayList;
import java.util.List;

public class GroupDTO {

    private Long group_id;
    private String name;
    private String details;
    private String winner;
    private String last_updated;

    private List<MatchDTO> matches;

    public GroupDTO(Long group_id, String name, String details, String winner, String last_updated) {
        this.group_id = group_id;
        this.name = name;
        this.details = details;
        this.winner = winner;
        this.last_updated = last_updated;


        this.matches = new ArrayList<MatchDTO>();
    }

    public GroupDTO() {
        this.matches = new ArrayList<MatchDTO>();
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public List<MatchDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchDTO> matches) {
        this.matches = matches;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "group_id=" + group_id +
                ", name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", winner='" + winner + '\'' +
                '}';
    }
}
