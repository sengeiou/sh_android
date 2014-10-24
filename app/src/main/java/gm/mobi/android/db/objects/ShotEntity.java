package gm.mobi.android.db.objects;

public class ShotEntity extends Synchronized{

    private Long idShot;
    private Long idUser;
    private String comment;

    private UserEntity mUserObject;

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

    public UserEntity getUser() {
        return mUserObject;
    }

    public void setUser(UserEntity mUser) {
        this.mUserObject = mUser;
    }
}
