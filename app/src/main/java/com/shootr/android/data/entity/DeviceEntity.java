package com.shootr.android.data.entity;

public class DeviceEntity {

    private String idDevice;
    private Integer platform;
    private String token;
    private String uniqueDeviceID;
    private String model;
    private String osVer;
    private String appVer;
    private String locale;

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUniqueDeviceID() {
        return uniqueDeviceID;
    }

    public void setUniqueDeviceID(String uniqueDeviceID) {
        this.uniqueDeviceID = uniqueDeviceID;
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

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
