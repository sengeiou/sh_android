package com.shootr.mobile.data.entity;

public class FollowEntity {

    private String idFollowedUser;
    private boolean isFollowing;
    private String type;

    public String getIdFollowedUser() {
        return idFollowedUser;
    }

    public void setIdFollowed(String idFollowedUser) {
        this.idFollowedUser = idFollowedUser;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
