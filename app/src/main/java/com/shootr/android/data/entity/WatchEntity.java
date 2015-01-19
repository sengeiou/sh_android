package com.shootr.android.data.entity;

public class WatchEntity extends Synchronized{

    public static final Long STATUS_DEFAULT = 0L;
    public static final Long STATUS_WATCHING = 1L;
    public static final Long STATUS_REJECT = 2L;

    public static final Long VISIBLE = 1L;
    public static final Long NOT_VISIBLE = 0L;

    public static final Integer NOTIFICATION_ON = 1;
    public static final Integer NOTIFICATION_OFF = 0;

    private Long idEvent;
    private Long idUser;
    private Long status;
    private String place;
    private Boolean visible;
    private Integer notification;

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
        this.idEvent = idEvent;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        WatchEntity that = (WatchEntity) o;
        if (idEvent != null ? !idEvent.equals(that.idEvent) : that.idEvent != null){
            return false;
        }
        if (idUser != null ? !idUser.equals(that.idUser) : that.idUser != null){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = idEvent.hashCode();
        result = 31 * result + idUser.hashCode();
        return result;
    }

    public Integer getNotification() {
        return notification;
    }

    public void setNotification(Integer notification) {
        this.notification = notification;
    }
}
