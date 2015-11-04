package com.shootr.mobile.domain.utils;

public interface DeviceFactory {

    com.shootr.mobile.domain.Device createDevice();

    com.shootr.mobile.domain.Device updateDevice(com.shootr.mobile.domain.Device device);
}
