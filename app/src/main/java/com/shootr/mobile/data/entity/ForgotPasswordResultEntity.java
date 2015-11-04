package com.shootr.mobile.data.entity;

public class ForgotPasswordResultEntity {

    private String idUser;
    private String userName;
    private String emailEncrypted;
    private String photo;

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

    public String getEmailEncrypted() {
        return emailEncrypted;
    }

    public void setEmailEncrypted(String emailEncrypted) {
        this.emailEncrypted = emailEncrypted;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForgotPasswordResultEntity)) return false;

        ForgotPasswordResultEntity that = (ForgotPasswordResultEntity) o;

        if (idUser != null ? !idUser.equals(that.idUser) : that.idUser != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (emailEncrypted != null ? !emailEncrypted.equals(that.emailEncrypted) : that.emailEncrypted != null) {
            return false;
        }
        return !(photo != null ? !photo.equals(that.photo) : that.photo != null);
    }

    @Override public int hashCode() {
        int result = idUser != null ? idUser.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (emailEncrypted != null ? emailEncrypted.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        return result;
    }
}
