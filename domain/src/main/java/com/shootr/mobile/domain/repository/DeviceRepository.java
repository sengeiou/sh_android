package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Device;

public interface DeviceRepository {

    Device getCurrentDevice();

    Device putDevice(Device device);
}
