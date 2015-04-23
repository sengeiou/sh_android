package com.shootr.android.data.entity;

public class UserCreateAccountEntity extends UserEntity {

    private String hashedPassword;

    public UserCreateAccountEntity() {
        super();
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
