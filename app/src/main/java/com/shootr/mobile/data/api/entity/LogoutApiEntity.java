package com.shootr.mobile.data.api.entity;

public class LogoutApiEntity {

    private String idDevice;

    public LogoutApiEntity(String idDevice) {
        this.idDevice = idDevice;
    }

    public String getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }
}
