package gm.mobi.android.db.objects;

public class Shot extends Synchronized{

    private Long idShot;
    private Long idUser;
    private String comment;

    private User mUserObject;

    public Long getIdShot() {
        return idShot;
    }

    public void setIdShot(Long idShot) {
        this.idShot = idShot;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return mUserObject;
    }

    public void setUser(User mUser) {
        this.mUserObject = mUser;
    }
}
