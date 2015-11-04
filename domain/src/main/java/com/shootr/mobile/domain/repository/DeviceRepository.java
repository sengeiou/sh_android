package com.shootr.mobile.domain.repository;

public interface DeviceRepository {

    com.shootr.mobile.domain.Device getCurrentDevice();

    com.shootr.mobile.domain.Device putDevice(com.shootr.mobile.domain.Device device);
}
