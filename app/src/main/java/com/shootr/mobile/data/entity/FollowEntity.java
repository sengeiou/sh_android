package com.shootr.mobile.data.entity;

public class FollowEntity extends Synchronized{

    public static final int RELATIONSHIP_NONE = 0;
    public static final int RELATIONSHIP_OWN = 1;
    public static final int RELATIONSHIP_FOLLOWING = 2;
    public static final int RELATIONSHIP_FOLLOWER = 3;
    public static final int RELATIONSHIP_BOTH = 4;

    private String mIdUser;
    private String mFollowedUser;

    public String getIdUser() {
        return mIdUser;
    }

    public void setIdUser(String idUser) {
        mIdUser = idUser;
    }

    public String getFollowedUser() {
        return mFollowedUser;
    }

    public void setFollowedUser(String followedUser) {
        mFollowedUser = followedUser;
    }
}
