package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.DeviceEntity;
import com.shootr.mobile.domain.model.Device;
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
        device.setUniqueDevideID(entity.getUniqueDeviceID());
        device.setPlatform(entity.getPlatform());
        device.setModel(entity.getModel());
        device.setOsVer(entity.getOsVer());
        device.setAppVer(entity.getAppVer());
        device.setLocale(entity.getLocale());
        device.setDeviceUUID(entity.getDeviceUUID());
        device.setApplicationId(entity.getApplicationId());
        device.setAdvertisingId(entity.getAdvertisingId());
        device.setTelephoneId(entity.getTelephoneId());
        device.setAttestation(entity.getAttestation());
        return device;
    }

    public DeviceEntity transform(Device device) {
        DeviceEntity entity = new DeviceEntity();
        entity.setIdDevice(device.getIdDevice());
        entity.setToken(device.getToken());
        entity.setUniqueDeviceID(device.getUniqueDevideID());
        entity.setPlatform(device.getPlatform());
        entity.setModel(device.getModel());
        entity.setOsVer(device.getOsVer());
        entity.setAppVer(device.getAppVer());
        entity.setLocale(device.getLocale());
        entity.setDeviceUUID(device.getDeviceUUID());
        entity.setApplicationId(device.getApplicationId());
        entity.setAdvertisingId(device.getAdvertisingId());
        entity.setTelephoneId(device.getTelephoneId());
        entity.setAttestation(device.getAttestation());
        return entity;
    }
}
