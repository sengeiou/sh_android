package gm.mobi.android.db.objects;

public class Device extends Synchronized {

    private Long idDevice;
    private Long idUser;
    private String token;
    private String uniqueDevideID;
    private String model;
    private String osVer;

    public Long getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(Long idDevice) {
        this.idDevice = idDevice;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUniqueDevideID() {
        return uniqueDevideID;
    }

    public void setUniqueDevideID(String uniqueDevideID) {
        this.uniqueDevideID = uniqueDevideID;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOsVer() {
        return osVer;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }
}
