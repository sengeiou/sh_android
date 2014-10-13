package com.fav24.shootr.batch.optaData.rest.DTO;


import java.util.ArrayList;
import java.util.List;

public class CompetitionDTO {

    private Long competition_id; // si
    private Long ow_competition_id; //no

    private String name; // si
    private String teamtype;// filtrar por default, no guardar

    private String soccertype; // no, old
    private String format; // si

    private Long display_order; // si 
    private String type; // si

    private Long area_id; //si
    private String area_name; //no

    private String last_updated; //si 

    private List<SeasonDTO> seasons; //si


    public CompetitionDTO(Long competition_id, Long ow_competition_id, String name, String teamtype, String soccertype, String format, Long display_order, String type, Long area_id, String area_name, String last_updated) {
        this.competition_id = competition_id;
        this.ow_competition_id = ow_competition_id;
        this.name = name;
        this.teamtype = teamtype;
        this.soccertype = soccertype;
        this.format = format;
        this.display_order = display_order;
        this.type = type;
        this.area_id = area_id;
        this.area_name = area_name;
        this.last_updated = last_updated;
        this.seasons = new ArrayList<SeasonDTO>();
    }

    public CompetitionDTO() {
        this.seasons = new ArrayList<SeasonDTO>();
    }

    public Long getCompetition_id() {
        return competition_id;
    }

    public void setCompetition_id(Long competition_id) {
        this.competition_id = competition_id;
    }

    public Long getOw_competition_id() {
        return ow_competition_id;
    }

    public void setOw_competition_id(Long ow_competition_id) {
        this.ow_competition_id = ow_competition_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamtype() {
        return teamtype;
    }

    public void setTeamtype(String teamtype) {
        this.teamtype = teamtype;
    }

    public String getSoccertype() {
        return soccertype;
    }

    public void setSoccertype(String soccertype) {
        this.soccertype = soccertype;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Long getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(Long display_order) {
        this.display_order = display_order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getArea_id() {
        return area_id;
    }

    public void setArea_id(Long area_id) {
        this.area_id = area_id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public List<SeasonDTO> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<SeasonDTO> seasons) {
        this.seasons = seasons;
    }


    @Override
    public String toString() {
        return "CompetitionDTO{" +
                "competition_id=" + competition_id +
                ", ow_competition_id=" + ow_competition_id +
                ", name='" + name + '\'' +
                ", area_name='" + area_name + '\'' +
                '}';
    }
}
