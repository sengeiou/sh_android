package com.shootr.android.domain;

public class ForgotPasswordResult {

    private String idUser;
    private String userName;
    private String emailEncripted;

    public ForgotPasswordResult(String idUser, String userName, String emailEncripted) {
        this.idUser = idUser;
        this.userName = userName;
        this.emailEncripted = emailEncripted;
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
        if (!(o instanceof ForgotPasswordResult)) return false;

        ForgotPasswordResult that = (ForgotPasswordResult) o;

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
