package com.shootr.mobile.data.entity;

public class FollowEntity extends Synchronized {

    public static final int RELATIONSHIP_NONE = 0;
    public static final int RELATIONSHIP_OWN = 1;
    public static final int RELATIONSHIP_FOLLOWING = 2;
    public static final int RELATIONSHIP_FOLLOWER = 3;
    public static final int RELATIONSHIP_BOTH = 4;

    private String idUser;
    private String followedUser;
    private Long isFriend;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getFollowedUser() {
        return followedUser;
    }

    public void setFollowedUser(String followedUser) {
        this.followedUser = followedUser;
    }

    public Long isFriend() {
        return isFriend;
    }

    public void setIsFriend(Long isFriend) {
        this.isFriend = isFriend;
    }
}
