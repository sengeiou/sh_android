package com.shootr.mobile.data.entity;

public class FollowEntity {

    private String idFollowedUser;
    private boolean isFollowing;

    public String getIdFollowedUser() {
        return idFollowedUser;
    }

    public void setIdFollowedUser(String idFollowedUser) {
        this.idFollowedUser = idFollowedUser;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}
