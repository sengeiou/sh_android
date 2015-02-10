package com.shootr.android.data.entity;

public class WatchEntity extends Synchronized{

    public static final Long VISIBLE = 1L;
    public static final Long NOT_VISIBLE = 0L;

    private Long idEvent;
    private Long idUser;
    private String status;
    private Boolean visible;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isVisible() {
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
}
