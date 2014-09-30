package gm.mobi.android.db.objects;

public class Shot extends Synchronized{

    private Long mIdShot;
    private Long mIdUser;
    private String mComment;

    private User mUserObject;

    public Long getIdShot() {
        return mIdShot;
    }

    public void setIdShot(Long idShot) {
        mIdShot = idShot;
    }

    public Long getIdUser() {
        return mIdUser;
    }

    public void setIdUser(Long idUser) {
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
