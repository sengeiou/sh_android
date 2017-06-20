package com.shootr.mobile.domain.utils;

import com.shootr.mobile.domain.model.Device;

public interface DeviceFactory {

    Device createDevice();

    Device updateDevice(Device device);

    boolean needsUpdate(Device device);
}
