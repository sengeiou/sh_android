package com.shootr.android.domain;

public class Favorite {

    private String idUser;
    private String idEvent;
    private Integer order;


    public Favorite() {

    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favorite)) return false;

        Favorite favorite = (Favorite) o;

        if (idUser != null ? !idUser.equals(favorite.idUser) : favorite.idUser != null) return false;
        if (idEvent != null ? !idEvent.equals(favorite.idEvent) : favorite.idEvent != null) return false;
        return !(order != null ? !order.equals(favorite.order) : favorite.order != null);
    }

    @Override public int hashCode() {
        int result = idUser != null ? idUser.hashCode() : 0;
        result = 31 * result + (idEvent != null ? idEvent.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);
        return result;
    }
}
