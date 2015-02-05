package com.shootr.android.ui.model;


public class EventModel {

    private Long idEvent;
    private Long localTeamId;
    private Long visitorTeamId;
    private String datetime;
    private String title;
    private String picture;
    private long startDate;
    private long endDate;
    private boolean amIAuthor;

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
        this.idEvent = idEvent;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventModel that = (EventModel) o;

        if (!idEvent.equals(that.idEvent)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idEvent.hashCode();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public boolean amIAuthor() {
        return amIAuthor;
    }

    public void setAmIAuthor(boolean amIAuthor) {
        this.amIAuthor = amIAuthor;
    }
}
