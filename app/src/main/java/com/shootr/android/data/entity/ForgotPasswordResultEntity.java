package com.shootr.android.data.entity;

public class ForgotPasswordResultEntity {

    private String idUser;
    private String userName;
    private String emailEncripted;

    public ForgotPasswordResultEntity() {

    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailEncripted() {
        return emailEncripted;
    }

    public void setEmailEncripted(String emailEncripted) {
        this.emailEncripted = emailEncripted;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForgotPasswordResultEntity)) return false;

        ForgotPasswordResultEntity that = (ForgotPasswordResultEntity) o;

        if (idUser != null ? !idUser.equals(that.idUser) : that.idUser != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        return !(emailEncripted != null ? !emailEncripted.equals(that.emailEncripted) : that.emailEncripted != null);
    }

    @Override public int hashCode() {
        int result = idUser != null ? idUser.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (emailEncripted != null ? emailEncripted.hashCode() : 0);
        return result;
    }

}
