package com.shootr.android.data.entity;

public class DeviceEntity extends Synchronized {

    private String idDevice;
    private String idUser;
    private Integer platform;
    private String token;
    private String uniqueDevideID;
    private String model;
    private String osVer;

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
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

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }
}
