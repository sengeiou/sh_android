package com.shootr.android.data.entity;

public class FacebookUserEntity extends UserEntity {

    private Integer isNewUser;

    public Integer isNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(Integer isNewUser) {
        this.isNewUser = isNewUser;
    }
}
