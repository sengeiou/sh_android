package gm.mobi.android.db.objects;

import gm.mobi.android.db.GMContract;

/**
 * Created by InmaculadaAlcon on 17/09/2014.
 */
public class Shot extends Synchronized{

    private Integer idShot;
    private Integer idUser;
    private String comment;


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdShot() {
        return idShot;
    }

    public void setIdShot(Integer idShot) {
        this.idShot = idShot;
    }
}
