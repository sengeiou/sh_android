package com.shootr.android.data.entity;

import java.util.Date;

public class EventEntity extends Synchronized implements Comparable<EventEntity> {

    private String idEvent;
    private String idUser;
    private String userName;
    private String timezone;
    private String tag;
    private Date beginDate;
    private Date endDate;
    private String title;
    private String photo;
    private Integer notifyCreation;

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getNotifyCreation() {
        return notifyCreation;
    }

    public void setNotifyCreation(Integer notifyCreation) {
        this.notifyCreation = notifyCreation;
    }
}
