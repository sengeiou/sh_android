package com.shootr.android.ui.model;


public class MatchSearchResultModel {

    private Long idMatch;
    private String datetime;
    private String title;
    private Boolean addedAlready;

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
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

    public Boolean isAddedAlready() {
        return addedAlready;
    }

    public void setAddedAlready(Boolean addedAlready) {
        this.addedAlready = addedAlready;
    }
}
