package com.fav24.shootr.dao.domain;


public class Team extends Synchronized{

    private Long idTeam;
    private String clubName;
    private String officialName;
    private String shortName;
    private String tlaName;

    public Long getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(Long idTeam) {
        this.idTeam = idTeam;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTlaName() {
        return tlaName;
    }

    public void setTlaName(String tlaName) {
        this.tlaName = tlaName;
    }

    @Override
    public String toString() {
        return "Team{" +
                "idTeam=" + idTeam +
                ", clubName='" + clubName + '\'' +
                ", officialName='" + officialName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", tlaName='" + tlaName + '\'' +
                '}';
    }
}
