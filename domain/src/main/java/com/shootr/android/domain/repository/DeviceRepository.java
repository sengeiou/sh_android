package com.shootr.android.domain.repository;

import com.shootr.android.domain.Device;

public interface DeviceRepository {

    Device getCurrentDevice();

    Device putDevice(Device device);
}
