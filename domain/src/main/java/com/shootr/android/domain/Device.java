package com.shootr.android.domain;

public class Device {

    private String idDevice;
    private String idUser;
    private String token;
    private String uniqueDevideID;
    private String model;
    private String osVer;
    private Integer platform;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device)) return false;

        Device device = (Device) o;

        if (idDevice != null ? !idDevice.equals(device.idDevice) : device.idDevice != null) return false;
        if (idUser != null ? !idUser.equals(device.idUser) : device.idUser != null) return false;
        if (token != null ? !token.equals(device.token) : device.token != null) return false;
        if (uniqueDevideID != null ? !uniqueDevideID.equals(device.uniqueDevideID) : device.uniqueDevideID != null) {
            return false;
        }
        if (model != null ? !model.equals(device.model) : device.model != null) return false;
        if (osVer != null ? !osVer.equals(device.osVer) : device.osVer != null) return false;
        return !(platform != null ? !platform.equals(device.platform) : device.platform != null);
    }

    @Override
    public int hashCode() {
        int result = idDevice != null ? idDevice.hashCode() : 0;
        result = 31 * result + (idUser != null ? idUser.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (uniqueDevideID != null ? uniqueDevideID.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (osVer != null ? osVer.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        return result;
    }
}
