package com.fav24.shootr.batch.optaData.rest.DTO;


import java.util.ArrayList;
import java.util.List;

public class RoundDTO {

    private Long round_id;
    private String name;
    private String start_date;
    private String end_date;
    private String type;
    private Long groups;
    private String has_outgroup_matches; //no
    private String last_updated;

    private List<GroupDTO> groupList = null;
    private List<AggrDTO> agregations = null;
    private List<MatchDTO> matches = null;


    public RoundDTO() {
        this.groupList = new ArrayList<GroupDTO>();
        this.agregations = new ArrayList<AggrDTO>();
        this.matches = new ArrayList<MatchDTO>();
    }

    public RoundDTO(Long round_id, String name, String start_date, String end_date, String type, Long groups, String has_outgroup_matches, String last_updated) {
        this.round_id = round_id;
        this.name = name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.type = type;
        this.groups = groups;
        this.has_outgroup_matches = has_outgroup_matches;
        this.last_updated = last_updated;

        this.groupList = new ArrayList<GroupDTO>();
        this.agregations = new ArrayList<AggrDTO>();
        this.matches = new ArrayList<MatchDTO>();

    }

    public List<GroupDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupDTO> groupList) {
        this.groupList = groupList;
    }

    public List<AggrDTO> getAgregations() {
        return agregations;
    }

    public void setAgregations(List<AggrDTO> agregations) {
        this.agregations = agregations;
    }

    public List<MatchDTO> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchDTO> matches) {
        this.matches = matches;
    }

    public Long getRound_id() {
        return round_id;
    }

    public void setRound_id(Long round_id) {
        this.round_id = round_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getGroups() {
        return groups;
    }

    public void setGroups(Long groups) {
        this.groups = groups;
    }

    public String getHas_outgroup_matches() {
        return has_outgroup_matches;
    }

    public void setHas_outgroup_matches(String has_outgroup_matches) {
        this.has_outgroup_matches = has_outgroup_matches;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    @Override
    public String toString() {
        return "RoundDTO{" +
                "round_id=" + round_id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
