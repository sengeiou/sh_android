package com.shootr.android.domain;

public class ForgotPasswordResult {

    private String idUser;
    private String userName;
    private String emailEncripted;
    private String avatar;

    public ForgotPasswordResult() {

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForgotPasswordResult)) return false;

        ForgotPasswordResult that = (ForgotPasswordResult) o;

        if (idUser != null ? !idUser.equals(that.idUser) : that.idUser != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (emailEncripted != null ? !emailEncripted.equals(that.emailEncripted) : that.emailEncripted != null) {
            return false;
        }
        return !(avatar != null ? !avatar.equals(that.avatar) : that.avatar != null);
    }

    @Override public int hashCode() {
        int result = idUser != null ? idUser.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (emailEncripted != null ? emailEncripted.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ForgotPasswordResult{" +
          "idUser='" + idUser + '\'' +
          ", userName='" + userName + '\'' +
          ", emailEncripted='" + emailEncripted + '\'' +
          ", avatar='" + avatar + '\'' +
          '}';
    }
}
