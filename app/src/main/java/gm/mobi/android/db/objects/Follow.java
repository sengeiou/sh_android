package gm.mobi.android.db.objects;

public class Follow extends Synchronized{

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
