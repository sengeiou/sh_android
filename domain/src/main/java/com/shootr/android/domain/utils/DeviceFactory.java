package com.shootr.android.domain.utils;

import com.shootr.android.domain.Device;

public interface DeviceFactory {

    Device createDevice();

    Device updateDevice(Device device);
}
