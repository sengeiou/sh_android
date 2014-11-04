package com.shootr.android.ui.model;


public class MatchModel {

    private Long idMatch;
    private String localTeamName;
    private String visitorTeamName;
    private Long localTeamId;
    private Long visitorTeamId;
    private String datetime;
    private String title;
    private boolean isLive;

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
    }

    public String getLocalTeamName() {
        return localTeamName;
    }

    public void setLocalTeamName(String localTeamName) {
        this.localTeamName = localTeamName;
    }

    public String getVisitorTeamName() {
        return visitorTeamName;
    }

    public void setVisitorTeamName(String visitorTeamName) {
        this.visitorTeamName = visitorTeamName;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getVisitorTeamId() {
        return visitorTeamId;
    }

    public void setVisitorTeamId(Long visitorTeamId) {
        this.visitorTeamId = visitorTeamId;
    }

    public Long getLocalTeamId() {
        return localTeamId;
    }

    public void setLocalTeamId(Long localTeamId) {
        this.localTeamId = localTeamId;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
    }
}
