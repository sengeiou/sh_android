package com.shootr.android.domain;

import java.util.Comparator;

public class Favorite {

    private String idEvent;
    private Integer order;

    public Favorite() {

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

        if (idEvent != null ? !idEvent.equals(favorite.idEvent) : favorite.idEvent != null) return false;
        return !(order != null ? !order.equals(favorite.order) : favorite.order != null);
    }

    @Override public int hashCode() {
        int result = idEvent != null ? idEvent.hashCode() : 0;
        result = 31 * result + (order != null ? order.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Favorite{" +
          "idEvent='" + idEvent + '\'' +
          ", order=" + order +
          '}';
    }

    public static class AscendingOrderComparator implements Comparator<Favorite> {
        @Override public int compare(Favorite favorite, Favorite anotherFavorite) {
            return favorite.getOrder().compareTo(anotherFavorite.getOrder());
        }
    }
}
