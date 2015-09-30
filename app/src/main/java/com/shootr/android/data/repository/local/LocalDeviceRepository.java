package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.mapper.DeviceEntityMapper;
import com.shootr.android.db.manager.DeviceManager;
import com.shootr.android.domain.Device;
import com.shootr.android.domain.repository.DeviceRepository;
import javax.inject.Inject;

public class LocalDeviceRepository implements DeviceRepository {

    private final DeviceManager deviceManager;
    private final DeviceEntityMapper mapper;

    @Inject
    public LocalDeviceRepository(DeviceManager deviceManager, DeviceEntityMapper mapper) {
        this.deviceManager = deviceManager;
        this.mapper = mapper;
    }

    @Override
    public Device getCurrentDevice() {
        DeviceEntity existingDevice = deviceManager.getDevice();
        return mapper.transform(existingDevice);
    }

    @Override
    public Device putDevice(Device device) {
        deviceManager.saveDevice(mapper.transform(device));
        return device;
    }
}
