package com.shootr.mobile.domain;

public class Device {

    private String idDevice;
    private String token;
    private String uniqueDevideID;
    private String model;
    private String osVer;
    private Integer platform;
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
