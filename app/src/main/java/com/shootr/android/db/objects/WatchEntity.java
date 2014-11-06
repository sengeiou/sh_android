package com.shootr.android.db.objects;

public class WatchEntity extends Synchronized{

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_WATCHING = 1;
    public static final int STATUS_REJECT = 2;
    private Long idMatch;
    private Long idUser;
    private Long status;

    public Long getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(Long idMatch) {
        this.idMatch = idMatch;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WatchEntity that = (WatchEntity) o;

        if (idMatch != null ? !idMatch.equals(that.idMatch) : that.idMatch != null) return false;
        if (idUser != null ? !idUser.equals(that.idUser) : that.idUser != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idMatch.hashCode();
        result = 31 * result + idUser.hashCode();
        return result;
    }
}
