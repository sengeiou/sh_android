package com.shootr.mobile.domain.utils;

import com.shootr.mobile.domain.Device;

public interface DeviceFactory {

    Device createDevice();

    Device updateDevice(Device device);
}
