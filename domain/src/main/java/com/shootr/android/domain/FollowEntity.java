package com.shootr.android.domain;

public class FollowEntity extends Synchronized{

    public static final int RELATIONSHIP_NONE = 0;
    public static final int RELATIONSHIP_OWN = 1;
    public static final int RELATIONSHIP_FOLLOWING = 2;
    public static final int RELATIONSHIP_FOLLOWER = 3;
    public static final int RELATIONSHIP_BOTH = 4;

    private Long mIdUser;
    private Long mFollowedUser;

    public Long getIdUser() {
        return mIdUser;
    }

    public void setIdUser(Long idUser) {
        mIdUser = idUser;
    }

    public Long getFollowedUser() {
        return mFollowedUser;
    }

    public void setFollowedUser(Long followedUser) {
        mFollowedUser = followedUser;
    }
}
