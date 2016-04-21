package com.shootr.mobile.domain;

import java.util.Comparator;

public class Contributor {

    private String idUser;
    private String idStream;
    private String idContributor;
    private User user;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdStream() {
        return idStream;
    }

    public void setIdStream(String idStream) {
        this.idStream = idStream;
    }

    public String getIdContributor() {
        return idContributor;
    }

    public void setIdContributor(String idContributor) {
        this.idContributor = idContributor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contributor)) return false;

        Contributor that = (Contributor) o;

        if (!idUser.equals(that.idUser)) return false;
        if (!idStream.equals(that.idStream)) return false;
        if (!idContributor.equals(that.idContributor)) return false;
        return !(user != null ? !user.equals(that.user) : that.user != null);
    }

    @Override public int hashCode() {
        int result = idUser.hashCode();
        result = 31 * result + idStream.hashCode();
        result = 31 * result + idContributor.hashCode();
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    public static class AlphabeticContributorComparator implements Comparator<Contributor> {

        @Override public int compare(Contributor c1, Contributor c2) {
            return String.CASE_INSENSITIVE_ORDER.compare(c1.getUser().getName().trim(), c2.getUser().getName().trim());
        }
    }
}
