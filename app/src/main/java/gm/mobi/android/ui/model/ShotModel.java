package gm.mobi.android.ui.model;

import java.util.Date;

public class ShotModel {

    //Shot attributes
    private Long idShot;
    private String comment;
    private Date csys_birth;
    //User attributes
    private Long idUser;
    private String name;
    private String photo;

    public Long getIdShot() {
        return idShot;
    }

    public void setIdShot(Long idShot) {
        this.idShot = idShot;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCsys_birth() {
        return csys_birth;
    }

    public void setCsys_birth(Date csys_birth) {
        this.csys_birth = csys_birth;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
