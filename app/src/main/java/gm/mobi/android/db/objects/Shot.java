package gm.mobi.android.db.objects;

public class Shot extends Synchronized{

    private Integer mIdShot;
    private Integer mIdUser;
    private String mComment;

    private User mUserObject;

    public Integer getIdShot() {
        return mIdShot;
    }

    public void setIdShot(Integer idShot) {
        mIdShot = idShot;
    }

    public Integer getIdUser() {
        return mIdUser;
    }

    public void setIdUser(Integer idUser) {
        mIdUser = idUser;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public User getUser() {
        return mUserObject;
    }

    public void setUser(User mUser) {
        this.mUserObject = mUser;
    }
}
