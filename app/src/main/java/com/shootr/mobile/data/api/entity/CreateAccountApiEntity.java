package com.shootr.mobile.data.api.entity;

public class CreateAccountApiEntity {

    private String userName;
    private String email;
    private String password;
    private String language;

    public CreateAccountApiEntity(String userName, String email, String password, String language) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.language = language;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
