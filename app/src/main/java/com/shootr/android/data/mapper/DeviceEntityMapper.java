package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.domain.Device;
import javax.inject.Inject;

public class DeviceEntityMapper {

    @Inject public DeviceEntityMapper() {
    }

    public Device transform(DeviceEntity entity) {
        if (entity == null) {
            return null;
        }
        Device device = new Device();
        device.setIdDevice(entity.getIdDevice());
        device.setToken(entity.getToken());
        device.setUniqueDevideID(entity.getUniqueDevideID());
        device.setPlatform(entity.getPlatform());
        device.setModel(entity.getModel());
        device.setOsVer(entity.getOsVer());
        device.setAppVer(entity.getAppVer());
        device.setLocale(entity.getLocale());
        return device;
    }

    public DeviceEntity transform(Device device) {
        DeviceEntity entity = new DeviceEntity();
        entity.setIdDevice(device.getIdDevice());
        entity.setToken(device.getToken());
        entity.setUniqueDevideID(device.getUniqueDevideID());
        entity.setPlatform(device.getPlatform());
        entity.setModel(device.getModel());
        entity.setOsVer(device.getOsVer());
        entity.setAppVer(device.getAppVer());
        entity.setLocale(device.getLocale());
        return entity;
    }
}
