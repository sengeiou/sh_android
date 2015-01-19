package com.shootr.android.data.entity;

import java.util.Date;

public class EventEntity extends Synchronized implements Comparable<EventEntity> {

    private Long idEvent;
    private Date beginDate;
    private Date endDate;
    private Long idLocalTeam;
    private Long idVisitorTeam;
    private String title;

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
        this.idEvent = idEvent;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Long getIdLocalTeam() {
        return idLocalTeam;
    }

    public void setIdLocalTeam(Long idLocalTeam) {
        this.idLocalTeam = idLocalTeam;
    }

    public Long getIdVisitorTeam() {
        return idVisitorTeam;
    }

    public void setIdVisitorTeam(Long idVisitorTeam) {
        this.idVisitorTeam = idVisitorTeam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || !(o instanceof EventEntity)){
            return false;
        }
        EventEntity that = (EventEntity) o;

        if (idEvent != null ? !idEvent.equals(that.idEvent) : that.idEvent != null){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return idEvent != null ? idEvent.hashCode() : 0;
    }

    @Override public int compareTo(EventEntity another) {
        boolean areSameEvent = this.getIdEvent().equals(another.getIdEvent());
        if (areSameEvent) {
            return 0;
        }
        int dateComparison = this.getBeginDate().compareTo(another.getBeginDate());
        if (dateComparison == 0) {
            int idComparison = this.getIdEvent().compareTo(another.getIdEvent());
            return idComparison;
        } else {
            return dateComparison;
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
