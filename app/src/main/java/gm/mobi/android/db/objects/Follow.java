package gm.mobi.android.db.objects;

public class Follow extends Synchronized{

    public static final int RELATIONSHIP_NONE = 0;
    public static final int RELATIONSHIP_OWN = 1;
    public static final int RELATIONSHIP_FOLLOWING = 2;
    public static final int RELATIONSHIP_FOLLOWER = 3;
    public static final int RELATIONSHIP_BOTH = 4;

    private Integer mIdUser;
    private Integer mFollowedUser;

    public Integer getIdUser() {
        return mIdUser;
    }

    public void setIdUser(Integer idUser) {
        mIdUser = idUser;
    }

    public Integer getFollowedUser() {
        return mFollowedUser;
    }

    public void setFollowedUser(Integer followedUser) {
        mFollowedUser = followedUser;
    }
}
