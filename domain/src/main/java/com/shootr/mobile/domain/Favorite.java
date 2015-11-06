package com.shootr.mobile.domain;

import java.util.Comparator;

public class Favorite {

    private String idStream;
    private Integer order;

    public Favorite() {

    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
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

        if (idStream != null ? !idStream.equals(favorite.idStream) : favorite.idStream != null) return false;
        return !(order != null ? !order.equals(favorite.order) : favorite.order != null);
    }

    @Override public int hashCode() {
        int result = idStream != null ? idStream.hashCode() : 0;
        result = 31 * result + (order != null ? order.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Favorite{" +
          "idStream='" + idStream + '\'' +
          ", order=" + order +
          '}';
    }

    public static class AscendingOrderComparator implements Comparator<Favorite> {

        @Override public int compare(Favorite favorite, Favorite anotherFavorite) {
            return favorite.getOrder().compareTo(anotherFavorite.getOrder());
        }
    }
}
