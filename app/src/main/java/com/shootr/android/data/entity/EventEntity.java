package com.shootr.android.data.entity;

public class EventEntity extends Synchronized implements Comparable<EventEntity> {

    private String idEvent;
    private String idUser;
    private String userName;
    private String tag;
    private String title;
    private String photo;
    private Integer notifyCreation;
    private String locale;

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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
        int idComparison = this.getIdEvent().compareTo(another.getIdEvent());
        return idComparison;
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
